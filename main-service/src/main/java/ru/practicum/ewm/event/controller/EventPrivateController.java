package ru.practicum.ewm.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.pagination.MyPageRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService service;

    @Autowired
    public EventPrivateController(EventService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto save(@PathVariable Long userId,
                             @RequestBody @Valid NewEventDto eventDto) {
        log.info("Request received POST /users/userId={}/events: event {}", userId, eventDto);
        EventFullDto savedEvent = service.save(userId, eventDto);
        log.info("Request POST /users/userId={}/events processed: event={} is created", userId, savedEvent);
        return savedEvent;
    }

    @GetMapping
    public List<EventShortDto> getAllByUser(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Request received: GET /users/userId={}/events: from={}, size={}", userId, from, size);
        PageRequest pageRequest = new MyPageRequest(from, size, Sort.unsorted());
        List<EventShortDto> shortDtoList = service.getAllByUserId(userId, pageRequest);
        log.info("Request GET /users/userId={}/events processed:{}", userId, shortDtoList);
        return shortDtoList;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByUserAndEventId(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("Request received: GET /users/userId={}/events/eventId={}", userId, eventId);
        EventFullDto fullDto = service.getByUserAndEventId(userId, eventId);
        log.info("Request GET /users/userId={}/events/eventId={} processed:{}", userId, eventId, fullDto);
        return fullDto;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @RequestBody @Valid UpdateEventUserRequest updateEventDto) {
        log.info("Request received PATCH /users/userId={}/events/eventId={}: event {}",
                userId, eventId, updateEventDto);
        EventFullDto updatedEvent = service.update(userId, eventId, updateEventDto);
        log.info("Request PATCH /users/userId={}/events/eventId={} processed: event={} is updated",
                userId, eventId, updatedEvent);
        return updatedEvent;
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByEvent(@PathVariable Long userId,
                                               @PathVariable Long eventId) {
        log.info("Request received: GET /users/userId={}/events/eventId={}/requests", userId, eventId);
        List<RequestDto> requests = service.getRequestByEventId(userId, eventId);
        log.info("Request GET /users/userId={}/events/eventId={}/requests processed:{}", userId, eventId, requests);
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        log.info("Request received PATCH /users/userId={}/events/eventId={}/requests: request {}",
                userId, eventId, request);
        EventRequestStatusUpdateResult updatedRequests = service.updateRequestsStatus(userId, eventId, request);
        log.info("Request PATCH /users/userId={}/events/eventId={}/requests processed: event:{} is created",
                userId, eventId, updatedRequests);
        return updatedRequests;
    }

}
