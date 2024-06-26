package ru.practicum.ewm.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.StatDataCreateDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.ewm.model.StatData.DATE_FORMAT;

@JsonTest
class StatDataCreateDtoTest {

    @Autowired
    private JacksonTester<StatDataCreateDto> json;

    @Test
    void testStatDataCreateDto() throws IOException {
        StatDataCreateDto dto = StatDataCreateDto.builder()
                .appName("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .created(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .build();

        JsonContent<StatDataCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo(dto.getAppName());
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo(dto.getUri());
        assertThat(result).extractingJsonPathStringValue("$.ip").isEqualTo(dto.getIp());
        assertThat(result).extractingJsonPathStringValue("$.timestamp").isEqualTo(dto.getCreated());
    }
}