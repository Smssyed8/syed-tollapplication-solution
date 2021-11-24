package com.tollapplication.utils.mappers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
/**
 * Dto for mapping the yml data
 */
public class TimeFeeDto {

    private LocalTime start;
    private LocalTime end;
    private double fees;
    //defaulting to 18 if true
    private boolean isPeak;
}
