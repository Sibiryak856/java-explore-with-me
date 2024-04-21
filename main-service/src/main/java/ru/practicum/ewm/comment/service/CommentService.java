package ru.practicum.ewm.comment.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.model.CommentState;
import ru.practicum.ewm.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {
    CommentDto save(Long userId, Long eventId, CommentRequestDto createDto);

    CommentDto update(Long userId, Long eventId, Long commentId, CommentRequestDto commentDto);
    
    void delete(Long commentId);

    List<CommentDto> getAll(List<Long> users, List<CommentState> states, List<Long> events, LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest);
}
