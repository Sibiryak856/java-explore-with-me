package ru.practicum.ewm.server.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.stats.StatDataCreateDto;
import ru.practicum.ewm.dto.stats.StatDataDto;
import ru.practicum.ewm.server.stats.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {

    public final StatRepository statRepository;

    @Autowired
    public StatServiceImpl(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    @Override
    public void save(StatDataCreateDto createDto) {

    }

    @Override
    public StatDataDto findAllByQuery(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return null;
    }
}
