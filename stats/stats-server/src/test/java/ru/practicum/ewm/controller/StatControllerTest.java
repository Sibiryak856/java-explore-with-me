package ru.practicum.ewm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.StatDataResponseDto;
import ru.practicum.ewm.service.StatServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatController.class)
class StatControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private StatServiceImpl service;

    @Autowired
    private MockMvc mvc;


    @Test
    void save_whenDataIsValid_thenReturnIsCreatedAndResponseDto() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        StatDataCreateDto dto = StatDataCreateDto.builder()
                .appName("app")
                .uri("uri")
                .ip("ip")
                .created(now)
                .build();
        StatDataResponseDto responseDto = StatDataResponseDto.builder()
                .appName("app")
                .uri("uri")
                .ip("ip")
                .created(now)
                .build();
        when(service.save(any(StatDataCreateDto.class)))
                .thenReturn(responseDto);

        String result = mvc.perform(post("/hit")
                        .content(String.valueOf(mapper.writeValueAsString(dto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(result).isEqualTo(mapper.writeValueAsString(responseDto));
    }

    @Test
    void save_whenDataIsNotValid_thenReturnIsBadRequest() throws Exception {
        StatDataCreateDto dto = StatDataCreateDto.builder()
                .appName(null)
                .uri("uri")
                .ip("ip")
                .created(LocalDateTime.now())
                .build();

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