package com.tollapplication.service;

import com.tollapplication.exceptions.TollApplicationException;
import com.tollapplication.model.Vehicle;
import com.tollapplication.utils.constants.ErrorCodes;
import com.tollapplication.utils.constants.ServiceConstants;

import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.tollapplication.utils.constants.ServiceConstants.MAX_FEE;

public class TollFeeCalculator {

    private GetTimedFeeService getTimedFeeService;

    private FreePassService freePassService;

    public TollFeeCalculator() {
        this.getTimedFeeService = new GetTimedFeeServiceImpl();
        this.freePassService = new FreePassServiceImpl();
    }

    /**
     * validate data passed
     * @param vehicle
     * @param dates
     * @return
     */
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

    /**
     * Check if free day or free vehicle
     * @param vehicle
     * @param dates
     * @return
     */
    private boolean isFreePass(Vehicle vehicle, LocalDateTime[] dates) {
        //we can use this to throw exception with description
       return freePassService.isTollFreeVehicle(vehicle) || freePassService.isFreeDay(dates[0].toLocalDate());
    }

    /**
     * get Toll Fee, validate data
     * @param vehicle
     * @param dates
     * @return
     */
    public int getTollFee(Vehicle vehicle, LocalDateTime... dates) {

        if(!isValidData(vehicle, dates)) return 0;
        if(isFreePass(vehicle, dates)) return 0;

        //can use set/Treeset, but use of index is good way for tmpTime, and also Set uses hash internally,
        //which is extra operation internally, since we only need to iterate all elements in list
        List<LocalTime> list =  Arrays.stream(dates)
                .map(LocalDateTime::toLocalTime)
                .sorted()
                .distinct()
                .collect(Collectors.toList());

        if (list.isEmpty()) {
            return 0;
        }

        return calculateTollFee(list);
    }

    /**
     * Get toll fee based on criterias
     * max toll fee is 60 per day
     * if repeated within hour, select higher toll fee
     * skip free vehicles and free days
     * default fee 8, considering Indian scenario, where there is no 0 fee any time of day
     * @param list
     * @return
     */
    private int calculateTollFee(List<LocalTime> list){

        double totalFee;
        try{
            /* Time related variables, temporaryTime, currentTime in iteration, nextHour = currentTime + 60 min*/
            LocalTime tmpTime = list.get(0);
            LocalTime nextHour = tmpTime.plusMinutes(ServiceConstants.SIXTY_MINUTES);
            LocalTime currentTime;

            /* Fee related variables, temporaryFee, totalFee and currentFee in iteration */
            double tmpFee = getTimedFeeService.getFeeForTime(tmpTime);
            totalFee = tmpFee;
            double currentFee;

            for (LocalTime listVal: list) {
                //skipping tmpTime comparing
                if(listVal.equals(tmpTime)){
                    continue;
                }

                currentTime = listVal;
                currentFee = getTimedFeeService.getFeeForTime(currentTime);

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

                //commonolizing and this snippet is for totalFee
                totalFee += tmpFee;  //13 18 18 36 36 44
                if (totalFee >= MAX_FEE) {
                    return MAX_FEE;
                }
            }
        } catch(Exception e){
            throw new TollApplicationException(e.getMessage());
        }
        return Math.min(((int) totalFee), MAX_FEE);
    }
}
