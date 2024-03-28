package ru.practicum.ewm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.StatDataResponseDto;
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
    public StatDataResponseDto save(StatDataCreateDto createDto) {
        return mapper.toResponseDto(statRepository.save(mapper.toStatData(createDto)));
    }

    @Override
    public List<ViewStatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        Pageable pageable = PageRequest.of(5 / 10, 10, Sort.by(Sort.Direction.DESC, "hits"));
        if (unique) {
            if (uris == null) {
                return mapper.toListStatDataDto(
                        statRepository.findAllByTimeBetweenAndUniqueHit(start, end, pageable));
            } else {
                return mapper.toListStatDataDto(
                        statRepository.findAllByTimeBetweenAndUniqueHitAndUriIn(start, end, uris, pageable));
            }
        } else {
            if (uris == null) {
                return mapper.toListStatDataDto(
                        statRepository.findAllByTimeBetween(start, end, pageable));
            }
            else {
                return mapper.toListStatDataDto(
                        statRepository.findAllByTimeBetweenAndUriIn(start, end, uris, pageable));
            }
        }
    }
}
