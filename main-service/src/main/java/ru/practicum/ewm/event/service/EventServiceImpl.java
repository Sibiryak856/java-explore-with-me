package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentState;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.controller.SortQuery;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.requestModel.EventAdminRequest;
import ru.practicum.ewm.event.requestModel.EventPublicRequest;
import ru.practicum.ewm.exception.NotAccessException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestsCountDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.Constants.FORMATTER;
import static ru.practicum.ewm.event.model.EventState.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final CommentRepository commentRepository;
    private final StatsClient client;

    @Transactional
    @Override
    public EventFullDto save(long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        long categoryId = eventDto.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category id=%d not found", categoryId)));
        Event event = eventRepository.save(
                eventMapper.toEvent(eventDto, user, category, EventState.PENDING));
        return eventMapper.toFullDto(event, null, null, null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllByUserId(long userId, PageRequest pageRequest) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> viewStatMap = getEventViews(events);

        Map<Long,Long> confirmedRequestMap = getConfirmedRequests(events);

        return eventMapper.toEventShortDtoListWithSortByViews(events, viewStatMap, confirmedRequestMap);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getByUserAndEventId(long userId, long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        Map<Long, Long> viewStatMap = getEventViews(List.of(event));

        Map<Long,Long> confirmedRequestMap = getConfirmedRequests(List.of(event));

        Map<Long, List<Comment>> comments = getComments(List.of(event));

        return eventMapper.toFullDto(event,
                viewStatMap.getOrDefault(eventId, 0L),
                confirmedRequestMap.getOrDefault(eventId, 0L),
                comments.getOrDefault(eventId, Collections.emptyList()));
    }

    @Transactional
    @Override
    public EventFullDto update(long userId, long eventId, UpdateEventUserRequest updateEventDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getState().equals(PUBLISHED)) {
            throw new NotAccessException("Only pending or canceled events can be changed");
        }
        if (updateEventDto.isStateNeedUpdate()) {
            switch (updateEventDto.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(PENDING);
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unknown state action: \"" + updateEventDto.getStateAction() + "\"");
            }
        }

        Event updatedEvent = eventRepository.save(
                eventMapper.update(updateEventDto, event));

        return eventMapper.toFullDto(updatedEvent, null, null, null);

    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAll(EventAdminRequest request, PageRequest pageRequest) {
        QEvent event = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();
        if (request.getUsers() != null && !request.getUsers().isEmpty()) {
            conditions.add(event.initiator.id.in(request.getUsers()));
        }
        if (request.getStates() != null && !request.getStates().isEmpty()) {
            conditions.add(event.state.in(request.getStates()));
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            event.category.id.in(request.getCategories());
        }
        if (request.getRangeStart() != null && request.getRangeEnd() != null) {
            if (request.getRangeStart().isAfter(request.getRangeEnd())) {
                conditions.add(event.eventDate.between(request.getRangeStart(), request.getRangeEnd()));
            }
        }

        List<Event> events;

        if (conditions.isEmpty()) {
            events = eventRepository.findAll(pageRequest).getContent();
        } else {
            BooleanExpression exp = conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();
            events = eventRepository.findAll(exp, pageRequest).getContent();
        }

        Map<Long, Long> viewStatMap = getEventViews(events);

        Map<Long,Long> confirmedRequestMap = getConfirmedRequests(events);


        return eventMapper.toEventFullDtoList(events, viewStatMap, confirmedRequestMap);
    }

    @Transactional
    @Override
    public EventFullDto moderate(long eventId, UpdateEventAdminRequest updateEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (!event.getState().equals(EventState.PENDING)) {
            throw new NotAccessException(
                    String.format("Cannot publish the event because it's not in the right state: %s",
                            event.getState()));
        }
        if (event.getEventDate().plusHours(1)
                .isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("EventDate must start in at least 1 hour");
        }

        if (updateEventDto.isStateNeedUpdate()) {
            switch (updateEventDto.getStateAction()) {
                case REJECT_EVENT:
                    event.setState(CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(PUBLISHED);
                    event.setPublishedOn(
                            LocalDateTime.now().withNano(0));
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unknown state action: \"" + updateEventDto.getStateAction() + "\"");
            }
        }

        Event moderatedEvent = eventRepository.save(
                eventMapper.update(updateEventDto, event));

        return eventMapper.toFullDto(moderatedEvent, null, null, null);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getById(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException(String.format("Event id=%d not published yet", id));
        }

        Map<Long, Long> viewStatMap = getEventViews(List.of(event));

        Map<Long,Long> confirmedRequestMap = getConfirmedRequests(List.of(event));

        Map<Long, List<Comment>> comments = getComments(List.of(event));

        return eventMapper.toFullDto(event,
                viewStatMap.getOrDefault(id, 0L),
                confirmedRequestMap.getOrDefault(id, 0L),
                comments.getOrDefault(id, Collections.emptyList()));
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAll(EventPublicRequest request,
                                      String sort,
                                      PageRequest pageRequest) {
        QEvent event = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.state.eq(PUBLISHED));

        if (request.getText() != null) {
            conditions.add(event.annotation.containsIgnoreCase(request.getText())
                    .or(event.description.containsIgnoreCase(request.getText())));
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            conditions.add(event.category.id.in(request.getCategories()));
        }
        if (request.getPaid() != null) {
            conditions.add(event.paid.eq(request.getPaid()));
        }
        if (request.getRangeStart().isAfter(request.getRangeEnd())) {
            conditions.add(event.eventDate.between(request.getRangeStart(), request.getRangeEnd()));
        }

        BooleanExpression exp = conditions.stream()
                .filter(Objects::nonNull)
                .reduce(BooleanExpression::and)
                .get();

        List<Event> events = eventRepository.findAll(exp, pageRequest).getContent();

        Map<Long, Long> viewStatMap = getEventViews(events);

        Map<Long,Long> confirmedRequestMap = getConfirmedRequests(events);

        if (request.getOnlyAvailable()) {
            events = events.stream()
                    .filter(e ->
                            e.getParticipantLimit() > confirmedRequestMap.getOrDefault(e.getId(), 0L))
                    .collect(Collectors.toList());
        }

        if (sort != null && sort.equals(SortQuery.VIEWS.toString())) {
            return eventMapper.toEventShortDtoListWithSortByViews(events, viewStatMap, confirmedRequestMap);
        }

        return eventMapper.toEventShortDtoList(events, viewStatMap, confirmedRequestMap);
    }


    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getRequestByEventId(long userId, long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        return requestMapper.toDtoList(requestRepository.findAllByEventId(eventId));
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        Map<Long,Long> confirmedRequestMap = getConfirmedRequests(List.of(event));
        long participantLimit = event.getParticipantLimit();
        long currentCountParticipant = confirmedRequestMap.getOrDefault(eventId, 0L);

        if (participantLimit == currentCountParticipant) {
            throw new IllegalArgumentException("Limit of participation requests reached");
        }

        List<Request> updatingRequests = requestRepository.findAllByIdInAndStatusIs(
                request.getRequestIds(), RequestStatus.PENDING);
        if (updatingRequests.isEmpty() || updatingRequests.size() < request.getRequestIds().size()) {
            throw new IllegalArgumentException(
                    "Only request with status " + RequestStatus.PENDING + " can be changed");
        }
        for (Request r : updatingRequests) {
            if (participantLimit == currentCountParticipant) {
                r.setStatus(RequestStatus.REJECTED);
            }
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                r.setStatus(RequestStatus.CONFIRMED);
                currentCountParticipant++;
            } else {
                r.setStatus(RequestStatus.REJECTED);
            }
        }

        return requestMapper.toStatusUpdateResult(
                requestRepository.saveAll(updatingRequests));
    }

    private Map<Long, Long> getEventViews(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Long> eventUriAndIdMap = events.stream()
                .map(Event::getId)
                .collect(Collectors.toMap(id -> "/events/" + id, Function.identity()));

        List<ViewStatDto> stats = client.getStats(
                LocalDateTime.now().minusYears(10).format(FORMATTER),
                LocalDateTime.now().withNano(0).format(FORMATTER),
                List.copyOf(eventUriAndIdMap.keySet()),
                Boolean.TRUE);

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> eventUriAndIdMap.get(stat.getUri()), ViewStatDto::getHits)
                );
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        List<RequestsCountDto> confirmedRequests = requestRepository
                .findAllConfirmedByEventIdIn(eventIds, RequestStatus.CONFIRMED);

        return confirmedRequests.stream()
                .collect(Collectors.toMap(
                        RequestsCountDto::getEventId, RequestsCountDto::getCountRequests));

    }

    private Map<Long, List<Comment>> getComments(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByEventIdInAndStateIs(eventIds, CommentState.PUBLISHED);

        return comments.stream()
                .collect(Collectors.groupingBy(comment -> comment.getEvent().getId()));
    }
}
