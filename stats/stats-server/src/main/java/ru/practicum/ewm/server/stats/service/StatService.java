package ru.practicum.ewm.server.stats.service;

import ru.practicum.ewm.dto.stats.StatDataCreateDto;
import ru.practicum.ewm.dto.stats.StatDataDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void save(StatDataCreateDto createDto);

    List<StatDataDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
