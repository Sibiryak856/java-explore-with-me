package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.mapper.StatDataMapper;
import ru.practicum.ewm.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {

    public final StatRepository statRepository;
    private final StatDataMapper mapper;

    @Autowired
    public StatServiceImpl(StatRepository statRepository, StatDataMapper mapper) {
        this.statRepository = statRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(StatDataCreateDto createDto) {
        statRepository.save(mapper.toStatData(createDto));
    }

    @Override
    public List<ViewStatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return statRepository.findAllUniqueHitByTimeBetween(start, end, uris);
        } else {
            return statRepository.findAllByTimeBetween(start, end, uris);
        }
    }
}
