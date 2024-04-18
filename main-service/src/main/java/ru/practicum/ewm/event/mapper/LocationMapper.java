package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface LocationMapper {

    /*@Mapping(target = "id", ignore = true)
    Location toLocation(LocationDto dto);

    LocationDto toDto(Location location);*/
}
