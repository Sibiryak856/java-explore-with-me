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

    EventFullDto save(long userId, NewEventDto eventDto);

    List<EventShortDto> getAllByUserId(long userId, PageRequest pageRequest);

    EventFullDto getByUserAndEventId(long userId, long eventId);

    EventFullDto update(long userId, long eventId, UpdateEventUserRequest updateEventDto);

    List<RequestDto> getRequestByEventId(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(long userId,
                                                        long eventId,
                                                        EventRequestStatusUpdateRequest request);

    List<EventFullDto> getAllByAdmin(List<Long> users,
                                     List<EventState> statesList,
                                     List<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     PageRequest pageRequest);

    EventFullDto moderate(long eventId, UpdateEventAdminRequest updateEventDto);

    EventFullDto getById(long id);

    List<EventShortDto> getAllPublic(String text,
                               List<Long> categories,
                               Boolean paid,
                               LocalDateTime rangeStart,
                               LocalDateTime rangeEnd,
                               Boolean onlyAvailable,
                               String sort,
                               PageRequest pageRequest);
}
