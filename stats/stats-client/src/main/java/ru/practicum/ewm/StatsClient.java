package ru.practicum.ewm;

import java.util.List;

public interface StatsClient {

    void postStat(StatDataCreateDto createDto);

    List<ViewStatDto> getStats(String start, String end, List<String> uris, Boolean unique);
}
