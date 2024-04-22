package ru.practicum.ewm.comment.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@Slf4j
@Validated
@RequestMapping("/users/{userId}/events/comments")
@RequiredArgsConstructor
public class CommentPrivateController {

    private final CommentService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentDto save(@PathVariable long userId,
                           @RequestBody @Valid CommentRequestDto createDto) {
        log.info("Request received POST /users/userId={}/events/comments: comment {}",
                userId, createDto);
        CommentDto savedComment = service.save(userId, createDto);
        log.info("Request POST /users/userId={}/events/comments: processed: event={} is created",
                userId, savedComment);
        return savedComment;
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable long userId,
                             @PathVariable long commentId,
                             @RequestBody @Valid CommentRequestDto commentDto) {
        log.info("Request received PATCH /users/userId={}/events/comments/commentId={}: comment {}",
                userId, commentId, commentDto);
        CommentDto updatedComment = service.update(userId, commentId, commentDto);
        log.info("Request PATCH /users/userId={}/events/comments/commentId={}: comment={} is updated",
                userId, commentId, updatedComment);
        return updatedComment;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable long userId,
                       @PathVariable long commentId) {
        log.info("Request received DELETE /users/userId={}/events/comments/commentId={} ",
                userId, commentId);
        service.delete(commentId);
        log.info("Request DELETE /users/userId={}/events/comments/commentId={} processed:",
                userId, commentId);
    }
}
