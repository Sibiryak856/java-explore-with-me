package ru.practicum.ewm.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;

@RestController
@Validated
@Slf4j
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService service;

    @Autowired
    public PrivateEventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    public EventFullDto save(@RequestHeader long userId,
                             @RequestBody @Valid NewEventDto eventDto) {
        log.info("Request received POST /users/{userId}/events: userId={}, event: {}", userId, eventDto);
        EventFullDto savedEvent = service.save(userId, eventDto);
        log.info("Request POST /users/{userId}/events processed: event:{} is created", savedEvent);
        return savedEvent;
    }
}
