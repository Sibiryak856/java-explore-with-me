package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(value (basePackages = "client")
public class StatServer {

    public static void main(String[] args) {
        SpringApplication.run(StatServer.class, args);
    }

}
