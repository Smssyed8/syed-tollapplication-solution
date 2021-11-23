package com.tollapplication.utils.constants;

import java.io.Serializable;

public class ErrorCodes implements Serializable {
    //can be moved to ErrorCodes enum and properties
    public static final String DIFFERENT_DATES = "dates should not be different";

    public static final String INVALID_VEHICLE = "Invalid Vehicle";

    public static final String DATES_EMPTY = "dates can not be empty";

    public static final String INVALID_DATE = "Given date is invalid or not supported by application";

    public static final String INVALID_DATE_ER = "Invalid value for MonthOfYear (valid values 1 - 12): 0";

    public static final String INVALID_DATE_MNTH = "Invalid value for MonthOfYear (valid values 1 - 12): 13";

    public static final String DUPLICATE_KEYS = "Duplicate key for values %s and %s";
}
