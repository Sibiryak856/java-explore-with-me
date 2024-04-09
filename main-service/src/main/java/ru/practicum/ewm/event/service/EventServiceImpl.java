package ru.practicum.ewm.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.StatsClientImpl;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.model.Event;
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
import java.util.stream.Collectors;

import static org.hibernate.type.LocalTimeType.FORMATTER;

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
    private StatsClient client = new StatsClientImpl("http://localhost:9090", new RestTemplateBuilder());

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
        return eventMapper.toFullDtoBeforePublished(event);
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
        List<String> eventsUri = events.stream()
                .map(event -> String.format("/events/%d", event.getId()))
                .collect(Collectors.toList());
        List<ViewStatDto> viewStatList = client.getStats(
                LocalDateTime.MIN.format(FORMATTER),
                LocalDateTime.MAX.format(FORMATTER),
                eventsUri,
                Boolean.FALSE);
        return eventMapper.toEventShortDtoList(events, viewStatList);
    }

    @Override
    public EventFullDto getByEventId(long userId, long eventId) {
        if (!userRepository.existsById(userId)) {
            new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        List<ViewStatDto> viewStatDtos = client.getStats(
                event.getCreatedOn().format(FORMATTER),
                LocalDateTime.MAX.format(FORMATTER),
                List.of(String.format("/events/%d", event.getId())),
                Boolean.FALSE);
        EventFullDto fullDto = eventMapper.toFullDto(event);
        if (!viewStatDtos.isEmpty()) {
            fullDto.setViews(viewStatDtos.get(0).getHits());
        }
        return fullDto;
    }

    @Override
    public EventFullDto update(long userId, long eventId, EventUpdateDto updateEventDto) {
        if (!userRepository.existsById(userId)) {
            new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalArgumentException("Only pending or canceled events can be changed");
        }
        Event updatedEvent = eventRepository.save(
                eventMapper.update(updateEventDto, event));
        return eventMapper.toFullDtoBeforePublished(updatedEvent);

    }

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
        int currentCountParticipant = requestRepository.countByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED);

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
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                r.setStatus(RequestStatus.CONFIRMED);
                currentCountParticipant++;
            } else {
                r.setStatus(RequestStatus.REJECTED);
            }
        }
        List<Request> updatedRequest = requestRepository.saveAll(updatingRequests);

        return requestMapper.toStatusUpdateResult(updatedRequest);
    }
}
