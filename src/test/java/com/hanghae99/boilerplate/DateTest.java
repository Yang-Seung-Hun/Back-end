package com.hanghae99.boilerplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DateTest {

    public static LocalDateTime main(String[] args) {

        String date = "2022-03-10T05:43:41Z";
        date=date.replace("T","&").replace("Z","&");
        String pattern = "yyyy-MM-dd&HH:mm:ss&";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        LocalDateTime result = LocalDateTime.parse(date, formatter);

        return result;
    }
}
