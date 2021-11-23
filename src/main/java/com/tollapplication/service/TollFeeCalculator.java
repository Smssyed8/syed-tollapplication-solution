package com.tollapplication.service;

import com.tollapplication.exceptions.TollApplicationException;
import com.tollapplication.model.Vehicle;
import com.tollapplication.utils.constants.ErrorCodes;
import com.tollapplication.utils.constants.ServiceConstants;

import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.tollapplication.utils.constants.ServiceConstants.MAX_FEE;

public class TollFeeCalculator {

    private GetTimedFeeService getTimedFeeService;

    private FreePassService freePassService;

    public TollFeeCalculator() {
        this.getTimedFeeService = new GetTimedFeeServiceImpl();
        this.freePassService = new FreePassServiceImpl();
    }

    private boolean isValidData(Vehicle vehicle, LocalDateTime... dates) {
        if (null == vehicle) {
            throw new TollApplicationException(ErrorCodes.INVALID_VEHICLE);
        }
        if (null == dates || dates.length == 0) {
            throw new TollApplicationException(ErrorCodes.DATES_EMPTY);
        }
        if (!String.valueOf(dates[0].toLocalDate().getYear()).equalsIgnoreCase(ServiceConstants.YEAR)) {
            throw new TollApplicationException(ErrorCodes.INVALID_DATE);
        }
        if (Arrays.stream(dates)
                .map(LocalDateTime::toLocalDate)
                .anyMatch(date -> !date.equals(dates[0].toLocalDate()))) {
            throw new TollApplicationException(ErrorCodes.DIFFERENT_DATES);
        }
        return true;
    }

    private boolean isFreePass(Vehicle vehicle, LocalDateTime[] dates) {
        //we can use this to throw exception with description on
        //throw new RuntimeException(" " +vehicle.getType()+" " +FREE_VEHICLE);
       return freePassService.isTollFreeVehicle(vehicle) || freePassService.isFreeDay(dates[0].toLocalDate());
    }

    /**
     * Get toll fee based on criterias
     * max toll fee is 60 per day
     * if repeated within hour, select higher toll fee
     * skip free vehicles and free days
     * default fee 8, considering Indian scenario, where there is no 0 fee any time of day
     * @param vehicle
     * @param dates
     * @return
     */
    public int getTollFee(Vehicle vehicle, LocalDateTime... dates) {

        if(!isValidData(vehicle, dates)) return 0;
        if(isFreePass(vehicle, dates)) return 0;

        //loading all data to map in same stream, to avoid another call to service in next iterations, maintaining O(n)
        TreeMap<LocalTime, Double> map =  Arrays.stream(dates)
                .map(time ->  time.toLocalTime())
                .sorted()
                .distinct()
                .collect(
                        Collectors.toMap(
                                time -> time,
                                time -> {
                                    try {
                                        return getTimedFeeService.getFeeForTime(time);
                                    } catch (Exception e) {
                                        throw new TollApplicationException(e.getMessage());
                                    }
                                },
                                (v1,v2) ->{ throw new TollApplicationException(String.format(ErrorCodes.DUPLICATE_KEYS, v1, v2));},
                                TreeMap::new
                        ));

        if (map.isEmpty()) {
            return 0;
        }

        Map.Entry<LocalTime, Double> entry = map.entrySet().iterator().next();
        LocalTime tmpTime = entry.getKey(), currentTime, nextHour;
        double tmpFee = entry.getValue(), currentFee;
        double totalFee = tmpFee;
        nextHour = tmpTime.plusMinutes(ServiceConstants.SIXTY_MINUTES);

        // 6, 50 //13  7, 0 // 13  7, 40 //18  7, 50 //13  7, 59 // 18 skip  18  8, 0 // 13  8, 29 //13 skip  8, 30 //8
        for (Map.Entry<LocalTime, Double> entryVal: map.entrySet()) {
            //skipping previousTime comparing
            if(entryVal.getKey().equals(tmpTime)){
                continue;
            }
            currentTime = entryVal.getKey();
            currentFee = entryVal.getValue();
            if(currentTime.isBefore(nextHour)){
                if(totalFee > 0){
                    totalFee -= tmpFee;  //0 0 0 18
                }
                if(currentFee > tmpFee) {
                    tmpFee = currentFee;
                }
            } else {
                nextHour = currentTime.plusMinutes(ServiceConstants.SIXTY_MINUTES);
                tmpFee = currentFee;
            }
            //commonolizing
            tmpTime = currentTime;
            totalFee += tmpFee;  //13 18 18 36 36 44

            if (totalFee >= MAX_FEE) {
                return MAX_FEE;
            }
        }
        return Math.min(((int) totalFee), MAX_FEE);
    }
}