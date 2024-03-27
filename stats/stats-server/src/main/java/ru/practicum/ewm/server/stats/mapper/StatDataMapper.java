package ru.practicum.ewm.server.stats.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.stats.StatDataCreateDto;
import ru.practicum.ewm.dto.stats.StatDataDto;
import ru.practicum.ewm.server.stats.model.StatData;
import ru.practicum.ewm.server.stats.model.ViewStats;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface StatDataMapper {

    StatData toStatData(StatDataCreateDto createDto);

    StatDataDto toStatDataDto(ViewStats viewStats);

    List<StatDataDto> toListStatDataDto(List<ViewStats> viewStatsList);

}
