package ru.practicum.ewm.service;

import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.StatDataResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    StatDataResponseDto save(StatDataCreateDto createDto);

    List<ViewStatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
