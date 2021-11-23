package com.tollapplication.service;

import com.tollapplication.model.Vehicle;
import com.tollapplication.utils.StaticDataUtil;
import com.tollapplication.utils.enums.TollFreeVehicles;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.stream.Stream;

public class FreePassServiceImpl implements FreePassService {

    /**
     * Check if weekend or holiday
     * @param day
     * @return
     */
    public boolean isFreeDay(LocalDate day) {
        if (day.getDayOfWeek() == DayOfWeek.SATURDAY || day.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return true;
        } else {
            return StaticDataUtil.holidaySet.contains(day);
        }
    }

    /**
     * check if vehicle is Toll free
     * @param vehicle
     * @return
     */
    public boolean isTollFreeVehicle(Vehicle vehicle) {
        return Stream.of(TollFreeVehicles.values())
                .anyMatch(v -> v.getCode().equalsIgnoreCase(vehicle.getType()));
    }

}