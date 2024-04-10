package ru.practicum.ewm.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    Compilation toCompilation(NewCompilationDto dto);

    CompilationDto toDto(Compilation compilation, List<EventShortDto> events);

    @Mapping(target = "id", ignore = true)
    Compilation update(NewCompilationDto dto, @MappingTarget Compilation compilation);
}
