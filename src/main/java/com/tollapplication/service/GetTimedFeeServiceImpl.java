package com.tollapplication.service;

import com.tollapplication.utils.StaticDataUtil;
import com.tollapplication.utils.mappers.TimeFeeDto;

import java.time.LocalTime;

public class GetTimedFeeServiceImpl implements GetTimedFeeService {

    /**
     * get fee for given time, in parallel, default fee to 8, considering Indian scenario
     * @param time
     * @return
     */
    @Override
    public double getFeeForTime(LocalTime time) {
       return StaticDataUtil.timeFeeDtosList
      .stream()
      .parallel()
      .filter(timeFeeDto -> isWithinTimeList(timeFeeDto, time))
      .findFirst()
      .map(TimeFeeDto::getFees)
      .orElse(8d);
    }

    /**
     * Find fee within start and end time for given time
     * @param timeFeeDto
     * @param time
     * @return
     */
    private boolean isWithinTimeList(TimeFeeDto timeFeeDto, LocalTime time) {
            LocalTime startTime = timeFeeDto.getStart();
            LocalTime endTime = timeFeeDto.getEnd();
            if(startTime.equals(time) || startTime.isBefore(time)){
                return endTime.equals(time) || endTime.isAfter(time);
            }
            return false;
    }
}
