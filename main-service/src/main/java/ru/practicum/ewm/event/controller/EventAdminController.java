package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.requestModel.EventAdminRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.pagination.CustomPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.Constants.DATE_FORMAT;

@RestController
@Validated
@Slf4j
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventService service;

    @GetMapping
    public List<EventFullDto> getAll(
            @RequestParam (required = false) List<Long> users,
            @RequestParam (required = false) List<String> states,
            @RequestParam (required = false) List<Long> categories,
            @RequestParam (required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam (required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Request received: GET /admin/events: " +
                "users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        List<EventState> statesList;
        if (states != null) {
            statesList = states.stream()
                    .map(EventState::from)
                    .collect(Collectors.toList());
        } else {
            statesList = null;
        }
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.unsorted());
        List<EventFullDto> fullDtoList = service.getAll(
                        new EventAdminRequest(users, statesList, categories, rangeStart, rangeEnd),
                        pageRequest);
        log.info("Request GET /admin/events processed:{}", fullDtoList);
        return fullDtoList;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto moderate(@PathVariable long eventId,
                                 @RequestBody @Valid UpdateEventAdminRequest updateEventDto) {
        log.info("Request received PATCH /admin/events/eventId={}: event {}", eventId, updateEventDto);
        EventFullDto moderatedEvent = service.moderate(eventId, updateEventDto);
        log.info("Request PATCH /admin/events/eventId={} processed: event:{} is moderated",
                eventId, moderatedEvent);
        return moderatedEvent;
    }

}
