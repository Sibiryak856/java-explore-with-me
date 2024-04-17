package ru.practicum.ewm;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class EwmApp {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final StatsClient CLIENT = new StatsClientImpl("${stats-server.url}");

    public static void main(String[] args) {
        SpringApplication.run(EwmApp.class, args);

    }
}
