package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventBaseRequest;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static ru.practicum.ewm.EwmApp.DATE_FORMAT;

@Component
@Mapper(componentModel = SPRING,
        imports = {LocalDateTime.class},
        uses = UserMapper.class)
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", defaultValue = "java(LocalDateTime.now().withNano(0))")
    Event toEvent(NewEventDto eventDto, User user, Category category, Location location, EventState state);

    @Mapping(target = "id", ignore = true)
    Event update(UpdateEventBaseRequest updateEventDto, @MappingTarget Event event);

    @Mapping(target = "createdOn", source = "createdOn", dateFormat = DATE_FORMAT)
    @Mapping(target = "eventDate", source = "eventDate", dateFormat = DATE_FORMAT)
    @Mapping(target = "publishedOn", source = "publishedOn", dateFormat = DATE_FORMAT, defaultValue = "null")
    @Mapping(target = "confirmedRequests", defaultValue = "0")
    @Mapping(target = "views", defaultValue = "0L")
    EventFullDto toFullDto(Event event, @Nullable Long views);

    @Mapping(target = "confirmedRequests", defaultValue = "0")
    @Mapping(target = "views", defaultValue = "0L")
    EventShortDto toShortDto(Event event, @Nullable Long views);

    default List<EventShortDto> toEventShortDtoListWithSortByViews(List<Event> events, Map<Long, Long> viewStatMap) {
        return events.stream()
                .map(event -> toShortDto(event, viewStatMap.get(event.getId())))
                .sorted((e1, e2) -> e2.getViews().compareTo(e1.getViews()))
                .collect(Collectors.toList());
    }

    default List<EventShortDto> toEventShortDtoList(List<Event> events, Map<Long, Long> viewStatMap) {
        return events.stream()
                .map(event -> toShortDto(event, viewStatMap.get(event.getId())))
                .collect(Collectors.toList());
    }

    default List<EventFullDto> toEventFullDtoList(List<Event> events, Map<Long, Long> viewStatMap) {
        return events.stream()
                .map(event -> toFullDto(event, viewStatMap.get(event.getId())))
                .collect(Collectors.toList());
    }

    default Map<Long, EventShortDto> toEventShortDtosMap(List<Event> events, Map<Long, Long> viewStatMap) {
        return events.stream()
                .map(event -> toShortDto(event, viewStatMap.get(event.getId())))
                .collect(Collectors.toMap(EventShortDto::getId, shortDto -> shortDto));
    } // may be <Compilation, List<EventShortDto>>

}
