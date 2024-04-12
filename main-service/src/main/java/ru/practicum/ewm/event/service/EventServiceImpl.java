package ru.practicum.ewm.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.LocationRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    public EventFullDto save(long userId, NewEventDto eventDto) {
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
    public List<EventShortDto> getAllByUserId(long userId, PageRequest pageRequest) {
        if (!userRepository.existsById(userId)) {
            new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> viewStatMap = getEventViews(events);

        return eventMapper.toEventShortDtos(events, viewStatMap);
    }

    @Override
    public EventFullDto getByUserAndEventId(long userId, long eventId) {
        if (!userRepository.existsById(userId)) {
            new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        Map<Long, Long> viewStatMap = EventServiceImpl.getEventViews(List.of(event));

        return eventMapper.toFullDto(event, viewStatMap.get(eventId));
    }

    @Override
    public EventFullDto update(long userId, long eventId, UpdateEventUserRequest updateEventDto) {
        if (!userRepository.existsById(userId)) {
            new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getState().equals(PUBLISHED)) {
            throw new IllegalArgumentException("Only pending or canceled events can be changed");
        }
        if (updateEventDto.isStateNeedUpdate()) {
            switch (updateEventDto.getStateAction()) {
                case CANCEL_REWIEW:
                    event.setState(CANCELLED);
                    break;
                case SEND_TO_REWIEW:
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
    public List<EventFullDto> getAllByAdmin(List<Long> users, List<EventState> statesList, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, PageRequest pageRequest) {
        return null;
    }

    @Override
    public EventFullDto moderate(long eventId, UpdateEventAdminRequest updateEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (!event.getState().equals(EventState.PENDING)) {
            throw new IllegalArgumentException(
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
                    event.setState(CANCELLED);
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
    public EventFullDto getById(long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));
        if (!event.getState().equals(PUBLISHED)) {
            throw new IllegalArgumentException(String.format("Event id=%d not published yet", id));
        }

        Map<Long, Long> viewStatMap = EventServiceImpl.getEventViews(List.of(event));

        return eventMapper.toFullDto(event, viewStatMap.get(id));
    }

    @Override
    public List<EventShortDto> getAll(String text, //ignoreCase
                                      List<Long> categories,
                                      Boolean paid,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Boolean onlyAvailable,
                                      PageRequest pageRequest) {
        return null;
    }

    //////////////////////////////////////////////////

    @Override
    public List<RequestDto> getRequestByEventId(long userId, long eventId) {
        if (!userRepository.existsById(userId)) {
            new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        if (!eventRepository.existsById(eventId)) {
            new NotFoundException(String.format("Event with id=%d was not found", eventId));
        }
        return requestMapper.toDtoList(requestRepository.findAllByEventId(eventId));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            long userId,
            long eventId,
            EventRequestStatusUpdateRequest request) {
        if (!userRepository.existsById(userId)) {
            new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));

        int participantLimit = event.getParticipantLimit();
        int currentCountParticipant = event.getConfirmedRequests();

        if (participantLimit == currentCountParticipant) {
            throw new IllegalArgumentException("Limit of participation requests reached");
        }

        List<Request> updatingRequests = requestRepository.findAllByIdIn(request.getRequestIds());
        for (Request r : updatingRequests) {
            if (!r.getStatus().equals(RequestStatus.PENDING)) {
                throw new IllegalArgumentException(
                        "Only request with status " + RequestStatus.PENDING + " can be changed");
            }
            if (participantLimit == currentCountParticipant) {
                r.setStatus(RequestStatus.REJECTED);
            }
            if (request.getStatus().equals(RequestStatus.PENDING)) {
                r.setStatus(RequestStatus.CONFIRMED);
                currentCountParticipant++;
                event.setConfirmedRequests(currentCountParticipant);
                eventRepository.save(event);
            } else {
                r.setStatus(RequestStatus.REJECTED);
            }
        }

        return requestMapper.toStatusUpdateResult(
                requestRepository.saveAll(updatingRequests));
    }

    public static Map<Long, Long> getEventViews(List<Event> events) {
        Map<String, Long> eventUriAndIdMap = events.stream()
                .map(Event::getId)
                .collect(Collectors.toMap(id -> "/events/" + id, Function.identity()));

        List<ViewStatDto> stats = CLIENT.getStats(
                LocalDateTime.MIN.format(FORMATTER),
                LocalDateTime.MAX.format(FORMATTER),
                List.copyOf(eventUriAndIdMap.keySet()),
                Boolean.FALSE);

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> eventUriAndIdMap.get(stat.getUri()), ViewStatDto:: getHits)
                );
    }
}
