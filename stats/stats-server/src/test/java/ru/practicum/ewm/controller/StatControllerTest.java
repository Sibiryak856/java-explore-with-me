package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.service.StatServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.model.StatData.DATE_FORMAT;

@WebMvcTest(controllers = StatController.class)
class StatControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private StatServiceImpl service;

    @Autowired
    private MockMvc mvc;

    private StatDataCreateDto dto;

    @BeforeEach
    void setUp() {
        dto = StatDataCreateDto.builder()
                .appName("app")
                .uri("uri")
                .ip("ip")
                .created(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .build();
    }


    @Test
    void save_whenDataIsValid_thenReturnIsCreatedAndResponseDto() throws Exception {

        mvc.perform(post("/hit")
                        .content(String.valueOf(mapper.writeValueAsString(dto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(service).save(any(StatDataCreateDto.class));
    }

    @Test
    void save_whenDataIsNotValid_thenReturnIsBadRequest() throws Exception {
        dto.setAppName(null);

        mvc.perform(post("/hit")
                        .content(String.valueOf(mapper.writeValueAsString(dto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any(StatDataCreateDto.class));
    }

    @Test
    void getHits_whenRequestIsValid_thenStatusIsOk() throws Exception {
        Long userId = 1L;
        when(service.getHits(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList(),
                anyBoolean()))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/stats", userId)
                        .param("start", "2020-05-05 00:00:00")
                        .param("end", "2035-05-05 00:00:00")
                        .param("uris", "/events")
                        .param("unique", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).getHits(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList(),
                anyBoolean());
    }
}