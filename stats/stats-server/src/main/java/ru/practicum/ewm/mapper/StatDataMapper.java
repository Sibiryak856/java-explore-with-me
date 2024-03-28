package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.StatDataResponseDto;
import ru.practicum.ewm.model.StatData;
import ru.practicum.ewm.model.ViewStats;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface StatDataMapper {

    @Mapping(target = "id", ignore = true)
    StatData toStatData(StatDataCreateDto createDto);

    StatDataResponseDto toResponseDto(StatData statData);

    List<ViewStatDto> toListStatDataDto(List<ViewStats> viewStatList);

}
