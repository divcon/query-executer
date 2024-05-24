package com.samsungcloud;

import java.time.LocalDateTime;

public class DirNameConvertor {
    private static final String FORMAT = "%d/%02d/%02d/%02d/%02d";

    private DirNameConvertor() {}

    public static String convertDateToDirectory(LocalDateTime dateTime) {
        return String.format(
                FORMAT,
                dateTime.getYear(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth(),
                dateTime.getHour(),
                dateTime.getMinute()
        );
    }
}
