package ru.practicum.ewm.request.service;

import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

public class RequestServiceImpl implements RequestService {

    public RequestRepository requestRepository;

    private UserRepository userRepository;

    private EventRepository eventRepository;

    private RequestMapper requestMapper;

    public RequestServiceImpl(RequestRepository requestRepository,
                              UserRepository userRepository,
                              EventRepository eventRepository,
                              RequestMapper requestMapper) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.requestMapper = requestMapper;
    }

    @Override
    public RequestDto save(long userId, long eventId) {
        if (!userRepository.existsById(userId)) {
            new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (requestRepository.findByRequesterIdAndEventId(userId, event).isPresent()) {
            throw new IllegalArgumentException("Request already exists");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new IllegalArgumentException("Can't add a request to your own event");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalArgumentException("Can't add a request to unpublished event");
        }
        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() ==
                        requestRepository.countByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED)) {
            throw new IllegalArgumentException("Limit of participation requests reached");
        }
        Request request = Request.builder()
                .eventId(eventId)
                .requesterId(userId)
                .status(RequestStatus.PENDING)
                .created(LocalDateTime.now().withNano(0))
                .build();
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%d was not found", requestId)));
        request.setStatus(RequestStatus.CANCELLED);
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    public List<RequestDto> getAllById(long userId) {
        if (!userRepository.existsById(userId)) {
            new NotFoundException(String.format("User with id=%d was not found", userId));
        }
        return requestMapper.toDtoList(requestRepository.findAllByRequesterId(userId));
    }
}
