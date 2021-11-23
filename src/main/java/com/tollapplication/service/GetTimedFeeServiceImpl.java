package com.tollapplication.service;

import com.tollapplication.exceptions.TollApplicationException;
import com.tollapplication.utils.StaticDataUtil;
import com.tollapplication.utils.constants.ServiceConstants;

import java.time.LocalTime;

public class GetTimedFeeServiceImpl implements GetTimedFeeService {

    /**
     * assuming the map will be properly configured in yml
     * assuming default value sometimes is skipped in yml
     * @param time
     * @return
     */
    @Override
    public double getFeeForTime(LocalTime time) {
         return time.getMinute() < 30 ?
                getFee(time, ServiceConstants.FIRST_HALF) : getFee(time, ServiceConstants.SECOND_HALF);
    }

    /**
     * Considering time Fee configured as per in timeBasedFeeMapping.yml
     * Default fee is 8
     * @param time
     * @param firstOrSecondHalf
     * @return
     */
    private Double getFee(LocalTime time, String firstOrSecondHalf) {
        try {
            return StaticDataUtil.timeFeeMap.containsKey(String.valueOf(time.getHour())) ?
                    (Double.valueOf(StaticDataUtil.timeFeeMap
                            .get(String.valueOf(time.getHour()))
                            .get(firstOrSecondHalf)
                            .get(ServiceConstants.FEE))) : ServiceConstants.DEFAULT_FEE;
        } catch(Exception e){
            throw new TollApplicationException(e.getMessage());
        }
    }

    //Another way of getting fee from list of timefee
    /*@Override
    public double getFeeForTime(LocalTime time) {
       return StaticDataUtil.timeFeeDtosList
      .stream()
      .parallel()
      .filter(timeFeeDto -> isWithinTimeList(timeFeeDto, time))
      .findFirst()
      .map(TimeFeeDto::getFees)
      .orElse(8d);
    }

    private boolean isWithinTimeList(TimeFeeDto timeFeeDto, LocalTime time) {
            LocalTime startTime = timeFeeDto.getStart();
            LocalTime endTime = timeFeeDto.getEnd();
            if(startTime.equals(time) || startTime.isBefore(time)){
                return endTime.equals(time) || endTime.isAfter(time);
            }
            return false;
    };*/

}
