package ru.practicum.ewm.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.ViewStatDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ViewStatDtoTest {

    @Autowired
    private JacksonTester<ViewStatDto> json;

    @Test
    void testViewStatDto() throws IOException {
        ViewStatDto dto = ViewStatDto.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .hits(6L)
                .build();

        JsonContent<ViewStatDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo(dto.getApp());
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo(dto.getUri());
        assertThat(result).extractingJsonPathNumberValue("$.hits").isEqualTo(6);
    }

}