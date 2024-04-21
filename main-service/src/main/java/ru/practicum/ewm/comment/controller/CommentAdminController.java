package ru.practicum.ewm.comment.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.model.CommentState;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.pagination.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.Constants.DATE_FORMAT;

@RestController
@Slf4j
@RequestMapping("/admin/comments")
public class CommentAdminController {

    private final CommentService service;

    @Autowired
    public CommentAdminController(CommentService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommentDto> getAll(
            @RequestParam (required = false) List<Long> users,
            @RequestParam (required = false) List<String> states,
            @RequestParam (required = false) List<Long> events,
            @RequestParam (required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam (required = false) @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Request received: GET /admin/comments: " +
                        "users={}, states={}, events={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, events, rangeStart, rangeEnd, from, size);
        List<CommentState> statesList;
        if (states != null) {
            statesList = states.stream()
                    .map(CommentState::from)
                    .collect(Collectors.toList());
        } else {
            statesList = null;
        }
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.unsorted());
        List<CommentDto> comments = service.getAll(users, statesList, events, rangeStart, rangeEnd, pageRequest);
        log.info("Request GET /admin/comments processed:{}", comments);
        return comments;
    }
}
