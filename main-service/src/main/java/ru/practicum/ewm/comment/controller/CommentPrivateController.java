package ru.practicum.ewm.comment.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@Slf4j
@RequestMapping("/users/{userId}/events/{eventId}/comments")
public class CommentPrivateController {

    private final CommentService service;

    @Autowired
    public CommentPrivateController(CommentService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentDto save(@PathVariable Long userId,
                           @PathVariable Long eventId,
                           @RequestBody @Valid CommentRequestDto createDto) {
        log.info("Request received POST /users/userId={}/events/eventId={}/comments: comment {}",
                userId, eventId, createDto);
        CommentDto savedComment = service.save(userId, eventId, createDto);
        log.info("Request POST /users/userId={}/events/eventId={}/comments: processed: event={} is created",
                userId, eventId, savedComment);
        return savedComment;
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable Long userId,
                             @PathVariable Long eventId,
                             @PathVariable Long commentId,
                             @RequestBody @Valid CommentRequestDto commentDto) {
        log.info("Request received PATCH /users/userId={}/events/eventId={}/comments/commentId={}: comment {}",
                userId, eventId, commentId, commentDto);
        CommentDto updatedComment = service.update(userId, eventId, commentId, commentDto);
        log.info("Request PATCH /users/userId={}/events/eventId={}/comments/commentId={}: comment={} is updated",
                userId, eventId, commentId, updatedComment);
        return updatedComment;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable Long userId,
                             @PathVariable Long eventId,
                             @PathVariable Long commentId) {
        log.info("Request received DELETE /users/userId={}/events/eventId={}/comments/commentId={} ",
                userId, eventId, commentId);
        service.delete(commentId);
        log.info("Request DELETE /users/userId={}/events/eventId={}/comments/commentId={} processed:",
                userId, eventId, commentId);
    }
}
