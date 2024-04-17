package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.controller.SortQuery;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
import ru.practicum.ewm.exception.NotAccessException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.ewm.EwmApp.CLIENT;
import static ru.practicum.ewm.EwmApp.FORMATTER;
import static ru.practicum.ewm.event.model.EventState.*;

@Service
public class EventServiceImpl implements EventService {

    public EventRepository eventRepository;
    private EventMapper eventMapper;
    private LocationMapper locationMapper;
    private LocationRepository locationRepository;
    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    private RequestRepository requestRepository;
    private RequestMapper requestMapper;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            EventMapper eventMapper,
                            LocationMapper locationMapper,
                            LocationRepository locationRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository,
                            RequestRepository requestRepository, RequestMapper requestMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
        this.locationMapper = locationMapper;
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.requestRepository = requestRepository;
        this.requestMapper = requestMapper;
    }

    @Transactional
    @Override
    public EventFullDto save(Long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User id=%d not found", userId)));
        long categoryId = eventDto.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category id=%d not found", categoryId)));
        Location location = locationRepository.save(locationMapper.toLocation(eventDto.getLocation()));
        Event event = eventRepository.save(
                eventMapper.toEvent(eventDto, user, category, location, EventState.PENDING));
        return eventMapper.toFullDto(event, null);
    }


    @Override
    public List<EventShortDto> getAllByUserId(Long userId, PageRequest pageRequest) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> viewStatMap = getEventViews(events);

        return eventMapper.toEventShortDtoListWithSortByViews(events, viewStatMap);
    }

    @Override
    public EventFullDto getByUserAndEventId(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        Map<Long, Long> viewStatMap = EventServiceImpl.getEventViews(List.of(event));

        return eventMapper.toFullDto(event, viewStatMap.get(eventId));
    }

    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventDto) {
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

        return eventMapper.toFullDto(updatedEvent, null);

    }

    @Override
    public List<EventFullDto> getAllByAdmin(List<Long> users,
                                            List<EventState> statesList,
                                            List<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            PageRequest pageRequest) {
        QEvent event = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            conditions.add(event.initiator.id.in(users));
        }
        if (statesList != null && !statesList.isEmpty()) {
            conditions.add(event.state.in(statesList));
        }
        if (categories != null && !categories.isEmpty()) {
            event.category.id.in(categories);
        }
        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                conditions.add(event.eventDate.between(rangeStart, rangeEnd));
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

        Map<Long, Long> viewStatMap = EventServiceImpl.getEventViews(events);

        return eventMapper.toEventFullDtoList(events, viewStatMap);
    }

    @Override
    public EventFullDto moderate(Long eventId, UpdateEventAdminRequest updateEventDto) {
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

        return eventMapper.toFullDto(moderatedEvent, null);
    }

    @Override
    public EventFullDto getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException(String.format("Event id=%d not published yet", id));
        }

        Map<Long, Long> viewStatMap = EventServiceImpl.getEventViews(List.of(event));

        return eventMapper.toFullDto(event, viewStatMap.get(id));
    }

    @Override
    public List<EventShortDto> getAllPublic(String text,
                                            List<Long> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            Boolean onlyAvailable,
                                            String sort,
                                            PageRequest pageRequest) {
        QEvent event = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.state.eq(PUBLISHED));

        if (text != null) {
            conditions.add(event.annotation.containsIgnoreCase(text)
                    .or(event.description.containsIgnoreCase(text)));
        }
        if (categories != null && !categories.isEmpty()) {
            conditions.add(event.category.id.in(categories));
        }
        if (paid != null) {
            conditions.add(event.paid.eq(paid));
        }
        if (rangeStart.isAfter(rangeEnd)) {
            conditions.add(event.eventDate.between(rangeStart, rangeEnd));
        }
        if (onlyAvailable) {
            conditions.add(event.confirmedRequests.lt(event.participantLimit));
        }

        BooleanExpression exp = conditions.stream()
                .filter(Objects::nonNull)
                .reduce(BooleanExpression::and)
                .get();

        List<Event> events = eventRepository.findAll(exp, pageRequest).getContent();

        Map<Long, Long> viewStatMap = EventServiceImpl.getEventViews(events);

        if (sort != null && sort.equals(SortQuery.VIEWS.toString())) {
            return eventMapper.toEventShortDtoListWithSortByViews(events, viewStatMap);
        }

        return eventMapper.toEventShortDtoList(events, viewStatMap);
    }


    @Override
    public List<RequestDto> getRequestByEventId(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        return requestMapper.toDtoList(requestRepository.findAllByEventId(eventId));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        int participantLimit = event.getParticipantLimit();
        int currentCountParticipant = event.getConfirmedRequests();

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
            event.setConfirmedRequests(currentCountParticipant);
            eventRepository.save(event);
        }

        return requestMapper.toStatusUpdateResult(
                requestRepository.saveAll(updatingRequests));
    }

    public static Map<Long, Long> getEventViews(List<Event> events) {
        if (events.isEmpty()) {
            return null;
        }
        Map<String, Long> eventUriAndIdMap = events.stream()
                .map(Event::getId)
                .collect(Collectors.toMap(id -> "/events/" + id, Function.identity()));

        List<ViewStatDto> stats = CLIENT.getStats(
                LocalDateTime.now().minusYears(10).format(FORMATTER),
                LocalDateTime.now().withNano(0).format(FORMATTER),
                List.copyOf(eventUriAndIdMap.keySet()),
                Boolean.TRUE);

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> eventUriAndIdMap.get(stat.getUri()), ViewStatDto::getHits)
                );
    }
}
