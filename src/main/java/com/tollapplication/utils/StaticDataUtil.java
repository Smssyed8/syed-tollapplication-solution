package com.tollapplication.utils;

import com.tollapplication.exceptions.TollApplicationException;
import com.tollapplication.service.GetTimedFeeServiceImpl;
import com.tollapplication.utils.constants.ServiceConstants;
import com.tollapplication.utils.mappers.TimeFeeDto;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

public class StaticDataUtil {
    private static final String HOLIDAY_YAML_FILE = "/holidays.yml";
    public static Set<LocalDate> holidaySet = new LinkedHashSet<LocalDate>();
    private static final String TIME_FEE_YAML_FILE = "/timeBasedFee.yml";
    public static List<TimeFeeDto> timeFeeDtosList = new ArrayList<>();

    /**
     * static block to load all static data
     */
    static {
        loadHolidayMap();
        loadTimeFeeList();
    }

    /**
     * Common code to read yaml file
     * @param filePath
     * @return
     */
    public static Object readYamlFile(String filePath) {
        try (InputStream in = GetTimedFeeServiceImpl.class.getResourceAsStream(filePath)) {
            Yaml yaml = new Yaml();
            return yaml.load(in);
        } catch (IOException e) {
            e.printStackTrace();
            throw new TollApplicationException(e.getMessage());
        }
    }

    /**
     * load all holidays list from yaml file
     */
    private static void loadHolidayMap() {
        try {
            Object obj = readYamlFile(HOLIDAY_YAML_FILE);
            if (obj instanceof LinkedHashMap) {
                LinkedHashMap map = (LinkedHashMap) obj;
                ArrayList<String> arrayList  = (ArrayList<String>)map.get(ServiceConstants.YEAR);
                holidaySet = arrayList.stream()
                        .map(holiday -> mapToDateSet((String) holiday))
                        .collect( Collectors.toSet());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new TollApplicationException(e.getMessage());
        }
    }

    /**
     * mapper to add holidays to set
     * @param holiday
     * @return
     */
    private static LocalDate mapToDateSet(String holiday) {
        return LocalDate.parse(holiday);
    };

    /**
     * map to time fee list
     * @param timeFeeString
     * @return
     */
    private static TimeFeeDto mapToTimeList(LinkedHashMap<String,String> timeFeeString) {
        return new TimeFeeDto()
                .setStart(LocalTime.parse(timeFeeString.get("start")))
                .setEnd(LocalTime.parse(timeFeeString.get("end")))
                .setFees(Double.valueOf(timeFeeString.get("fee")));
    }

    /**
     * load time based fee to list
     * unused, kept for improvement
     */
    private static void loadTimeFeeList() {
        try {
            Object obj = readYamlFile(TIME_FEE_YAML_FILE);
            if (obj instanceof ArrayList) {
                ArrayList<LinkedHashMap<String,String>> arrayList = (ArrayList) obj;
                arrayList.stream()
                        .map(timeFeeString -> mapToTimeList((LinkedHashMap<String,String>) timeFeeString))
                        .forEach(timeFeeDtosList::add);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}


