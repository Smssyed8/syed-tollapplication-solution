package com.tollapplication.service;

import com.tollapplication.model.Vehicle;
import java.time.LocalDate;

public interface FreePassService {

    boolean isFreeDay(LocalDate day);

    boolean isTollFreeVehicle(Vehicle vehicle);
}
