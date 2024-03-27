package ru.practicum.ewm.dto.stats;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class StatDataDtoTest {

    @Autowired
    private JacksonTester<StatDataDto> json;

    @Test
    void testStatDataDto() throws IOException {
        StatDataDto dto = StatDataDto.builder()
                .appName("ewm-main-service")
                .uri("/events/1")
                .hits(6L)
                .build();

        JsonContent<StatDataDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo(dto.getAppName());
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo(dto.getUri());
        assertThat(result).extractingJsonPathNumberValue("$.hits").isEqualTo(6);
    }

}