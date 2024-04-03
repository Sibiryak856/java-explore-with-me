package ru.practicum.ewm.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.mapper.StatDataMapper;
import ru.practicum.ewm.model.StatData;
import ru.practicum.ewm.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatServiceImplTest {

    @Mock
    private StatRepository repository;

    @Spy
    private StatDataMapper mapper = Mappers.getMapper(StatDataMapper.class);

    @InjectMocks
    private StatServiceImpl service;

    @Test
    void save() {
        StatDataCreateDto createDto = new StatDataCreateDto();
        StatData statData = new StatData();

        when(repository.save(any(StatData.class)))
                .thenReturn(statData);

        service.save(createDto);

        verify(repository).save(any(StatData.class));
    }

    @Test
    void getHits_whenIpNotUnique_thenFindAllByTimeBetween() {
        when(repository.findAllByTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList()))
                .thenReturn(Collections.emptyList());

        List<ViewStatDto> result = service.getHits(LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),
                List.of("uri"),
                FALSE);

        verify(repository).findAllByTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList());

        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void getHits_whenIpUnique_thenFindAllUniqueHitByTimeBetween() {
        when(repository.findAllUniqueHitByTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList()))
                .thenReturn(Collections.emptyList());

        List<ViewStatDto> result = service.getHits(LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),
                List.of("uri"),
                TRUE);

        verify(repository).findAllUniqueHitByTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList());

        assertThat(result).isEqualTo(Collections.emptyList());
    }
}