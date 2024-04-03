package ru.practicum.ewm.service;

import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void save(StatDataCreateDto createDto);

    List<ViewStatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);

}
