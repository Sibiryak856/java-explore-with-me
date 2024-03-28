package ru.practicum.ewm.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.StatDataResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class StatDataResponseDtoTest {

    @Autowired
    private JacksonTester<StatDataResponseDto> json;

    @Test
    void testStatDataResponseDto() throws IOException {
        LocalDateTime created = LocalDateTime.now();
        StatDataResponseDto dto = StatDataResponseDto.builder()
                .id(1L)
                .appName("ewm-main-service")
                .uri("/events/1")
                .ip("192.163.0.1")
                .created(created)
                .build();

        JsonContent<StatDataResponseDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo(dto.getAppName());
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo(dto.getUri());
        assertThat(result).extractingJsonPathStringValue("$.ip").isEqualTo(dto.getIp());
        assertThat(result).extractingJsonPathStringValue("$.timestamp")
                .isEqualTo(dto.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

}