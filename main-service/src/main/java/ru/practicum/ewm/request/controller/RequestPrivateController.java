package ru.practicum.ewm.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.transaction.Transactional;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/users/{userId}/requests")
public class RequestPrivateController {

    private final RequestService service;

    @Autowired
    public RequestPrivateController(RequestService service) {
        this.service = service;
    }

    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RequestDto save(@PathVariable Long userId,
                           @RequestParam Long eventId) {
        log.info("Request received: POST /users/userId={}/requests eventId={}", userId, eventId);
        RequestDto request = service.save(userId, eventId);
        log.info("Request POST /users/userId={}/requests eventId={} processed: {}", userId, eventId, request);
        return request;
    }

    @GetMapping
    public List<RequestDto> getAllByUser(@PathVariable Long userId) {
        log.info("Request received: GET /users/userId={}/requests:", userId);
        List<RequestDto> requests = service.getAllById(userId);
        log.info("Request GET /users/userId={}/requests processed: {}", userId, requests);
        return requests;
    }

    @Transactional
    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        log.info("Request received: PATCH /users/userId={}/requests/requestId={}/cancel:", userId, requestId);
        RequestDto request = service.cancelRequest(userId, requestId);
        log.info("Request PATCH /users/userId={}/requests/requestId={}/cancel processed: {}",
                userId, requestId, request);
        return request;
    }
}
