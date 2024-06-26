package ru.practicum.ewm.comment.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.pagination.CustomPageRequest;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService service;

    @GetMapping
    public List<CommentDto> getAllByEvent(
            @RequestParam Long eventId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Request received: GET /admin/comments/eventId={}:, from={}, size={}", eventId, from, size);
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.unsorted());
        List<CommentDto> comments = service.getAllPublishedByEvent(eventId, pageRequest);
        log.info("Request GET /admin/comments/eventId={} processed: comments={}", eventId, comments);
        return comments;
    }

    @GetMapping("/{commentId}")
    public CommentDto getById(@PathVariable long commentId) {
        log.info("Request received: GET /admin/comments/commentId={}:", commentId);
        CommentDto comment = service.getById(commentId);
        log.info("Request GET /admin/comments/commentId={} processed: comment={}", commentId, comment);
        return comment;
    }

}
