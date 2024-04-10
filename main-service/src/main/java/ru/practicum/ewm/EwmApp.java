package ru.practicum.ewm;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class EwmApp {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);

    public static final StatsClient CLIENT = new StatsClientImpl(
            "http://localhost:9090", new RestTemplateBuilder());

    public static void main(String[] args) {
        SpringApplication.run(EwmApp.class, args);
        /*StatsClient CLIENT = new StatsClientImpl("http://localhost:9090", new RestTemplateBuilder());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String created = now.format(formatter);

        StatDataCreateDto statData1 = StatDataCreateDto.builder()
                .appName("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .created(created)
                .build();
        StatDataCreateDto statData2 = StatDataCreateDto.builder()
                .appName("ewm-main-service")
                .uri("/events/2")
                .ip("192.163.0.1")
                .created(created)
                .build();
        StatDataCreateDto statData3 = StatDataCreateDto.builder()
                .appName("ewm-main-service")
                .uri("/events/1")
                .ip("292.163.0.1")
                .created(created)
                .build();
        StatDataCreateDto statData4 = StatDataCreateDto.builder()
                .appName("ewm-main-service")
                .uri("/events/1")
                .ip("292.163.0.1")
                .created(created)
                .build();

        CLIENT.postStat(statData1);
        CLIENT.postStat(statData2);
        CLIENT.postStat(statData3);
        CLIENT.postStat(statData4);

        List<ViewStatDto> list = CLIENT.getStats(
                now.minusHours(1).format(formatter),
                now.plusHours(1).format(formatter),
                List.of("/events/1"),
                Boolean.FALSE);
        System.out.println(list);*/
    }
}
