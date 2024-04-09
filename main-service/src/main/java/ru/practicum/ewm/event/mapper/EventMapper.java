package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static ru.practicum.ewm.EwmApp.DATE_FORMAT;

@Component
@Mapper(componentModel = SPRING,
        imports = {LocalDateTime.class})
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", defaultValue = "java(LocalDateTime.now().withNano(0))")
    Event toEvent(NewEventDto eventDto, User user, Category category, Location location, EventState state);

    @Mapping(target = "createdOn", source = "createdOn", dateFormat = DATE_FORMAT)
    @Mapping(target = "eventDate", source = "eventDate", dateFormat = DATE_FORMAT)
    @Mapping(target = "publishedOn", source = "publishedOn", dateFormat = DATE_FORMAT)
    @Mapping(target = "confirmedRequests", defaultValue = "0")
    @Mapping(target = "views", defaultValue = "0L")
    EventFullDto toFullDto(Event event);

    @Mapping(target = "createdOn", source = "createdOn", dateFormat = DATE_FORMAT)
    @Mapping(target = "eventDate", source = "eventDate", dateFormat = DATE_FORMAT)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "confirmedRequests", defaultValue = "0")
    @Mapping(target = "views", defaultValue = "0L")
        //check for NullPointer
    EventFullDto toFullDtoBeforePublished(Event event);


    @Mapping(target = "confirmedRequests", defaultValue = "0")
    @Mapping(target = "views", defaultValue = "0L")
    EventShortDto toShortDto(Event event);


    default List<EventShortDto> toEventShortDtoList(List<Event> events, List<ViewStatDto> viewStatList) {
        if (viewStatList.isEmpty()) {
            return events.stream()
                    .map(event -> toShortDto(event))
                    .collect(Collectors.toList());
        } else {
            Map<String, Long> viewStatMap = new HashMap<>();
            for (ViewStatDto v : viewStatList) {
                viewStatMap.put(v.getUri(), v.getHits());
            }
            List<EventShortDto> list = new ArrayList<>();
            events.stream()
                    .map(this::toShortDto)
                    .forEach(shortDto -> {
                        shortDto.setViews(
                                viewStatMap.getOrDefault(String.format("/events/%d", shortDto.getId()), 0L));
                        list.add(shortDto);
                    });
            return list;
        }
    }

    @Mapping(target = "id", ignore = true)
    Event update(EventUpdateDto updateEventDto, @MappingTarget Event event);
}
