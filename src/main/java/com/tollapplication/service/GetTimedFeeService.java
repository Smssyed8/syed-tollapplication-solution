package com.tollapplication.service;

import java.time.LocalTime;

public interface GetTimedFeeService {
    /**
     * get fee for given time
     * @return
     */
    double getFeeForTime(LocalTime time) throws Exception;
}
