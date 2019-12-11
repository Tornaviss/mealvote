package com.mealvote.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateTimeUtil {
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";

    public static final LocalTime DEADLINE = LocalTime.of(11, 0, 0);

    private DateTimeUtil() {

    }

    public static boolean isTimeToChangeMind(LocalDateTime ldt, LocalTime deadline) {
        return !ldt.toLocalDate().equals(LocalDate.now())
                    || ldt.toLocalTime().compareTo(deadline) < 0;
    }

}
