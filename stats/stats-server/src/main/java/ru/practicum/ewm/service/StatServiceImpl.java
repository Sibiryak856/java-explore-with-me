package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.mapper.StatDataMapper;
import ru.practicum.ewm.model.StatData;
import ru.practicum.ewm.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatServiceImpl implements StatService {

    public StatRepository statRepository;
    private StatDataMapper mapper;

    @Autowired
    public StatServiceImpl(StatRepository statRepository, StatDataMapper mapper) {
        this.statRepository = statRepository;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public StatData save(StatDataCreateDto createDto) {
        return statRepository.save(
                mapper.toStatData(createDto));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time must be before end");
        }
        if (unique) {
            return statRepository.findAllUniqueHitByTimeBetween(start, end, uris);
        }

        return statRepository.findAllByTimeBetween(start, end, uris);
    }
}
