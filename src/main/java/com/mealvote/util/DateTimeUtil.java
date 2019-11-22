package com.mealvote.util;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeUtil {
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static boolean isTimeToChangeMind(LocalDateTime ldt) {
        return !ldt.toLocalDate().equals(LocalDate.now())
                    || ldt.toLocalTime().compareTo(LocalTime.of(11, 0, 0)) < 0;
    }

}
