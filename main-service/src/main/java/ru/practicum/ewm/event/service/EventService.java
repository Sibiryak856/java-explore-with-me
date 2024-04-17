package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto save(Long userId, NewEventDto eventDto);

    List<EventShortDto> getAllByUserId(Long userId, PageRequest pageRequest);

    EventFullDto getByUserAndEventId(Long userId, Long eventId);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventDto);

    List<RequestDto> getRequestByEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(Long userId,
                                                        Long eventId,
                                                        EventRequestStatusUpdateRequest request);

    List<EventFullDto> getAllByAdmin(List<Long> users,
                                     List<EventState> statesList,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     PageRequest pageRequest);

    EventFullDto moderate(Long eventId, UpdateEventAdminRequest updateEventDto);

    EventFullDto getById(Long id);

    List<EventShortDto> getAllPublic(String text,
                               List<Long> categories,
                               Boolean paid,
                               LocalDateTime rangeStart,
                               LocalDateTime rangeEnd,
                               Boolean onlyAvailable,
                               String sort,
                               PageRequest pageRequest);
}
