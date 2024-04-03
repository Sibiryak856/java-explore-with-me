package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.StatDataCreateDto;
import ru.practicum.ewm.model.StatData;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static ru.practicum.ewm.model.StatData.DATE_FORMAT;

@Component
@Mapper(componentModel = SPRING)
public interface StatDataMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", source = "created", dateFormat = DATE_FORMAT)
    StatData toStatData(StatDataCreateDto createDto);
}
