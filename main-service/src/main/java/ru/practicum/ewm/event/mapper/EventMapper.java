package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface EventMapper {

    //@Mapping(target = "created", source = "created", dateFormat = DATE_FORMAT)
}
