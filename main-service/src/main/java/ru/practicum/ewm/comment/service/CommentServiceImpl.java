package ru.practicum.ewm.comment.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentState;
import ru.practicum.ewm.comment.model.QComment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotAccessException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    public CommentRepository commentRepository;

    private CommentMapper commentMapper;

    private UserRepository userRepository;
    private EventRepository eventRepository;

    @Transactional
    @Override
    public CommentDto save(Long userId, Long eventId, CommentRequestDto createDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event id=%d not found", eventId)));
        Comment comment = commentRepository.save(
                commentMapper.toComment(createDto, user, event, CommentState.PENDING, LocalDateTime.now()));
        return commentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public CommentDto update(Long userId, Long eventId, Long commentId, CommentRequestDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event id=%d not found", eventId)));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format("Comment id=%d not found", commentDto)));
        if (!comment.getState().equals(CommentState.PUBLISHED)) {
            throw new NotAccessException("Only pending or canceled events can be changed");
        }
        Comment updatedComment = commentRepository.save(
                commentMapper.update(commentDto, comment));
        return commentMapper.toCommentDto(updatedComment);
    }

    @Transactional
    @Override
    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getAll(List<Long> users, List<CommentState> states, List<Long> events, LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest) {
        QComment comment = QComment.comment;

        List<BooleanExpression> conditions = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            conditions.add(comment.author.id.in(users));
        }
        if (states != null && !states.isEmpty()) {
            conditions.add(comment.state.in(states));
        }
        if (events != null && !events.isEmpty()) {
            comment.event.id.in(events);
        }
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isBefore(rangeEnd)) {
                conditions.add(comment.created.between(rangeStart, rangeEnd));
            }
        }

        List<Comment> comments;
        if (conditions.isEmpty()) {
            comments = commentRepository.findAll(pageRequest).getContent();
        } else {
            BooleanExpression exp = conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();
            comments = commentRepository.findAll(exp, pageRequest).getContent();
        }
        return commentMapper.toListCommentDto(comments);
    }
}
