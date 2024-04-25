package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.requestModel.EventAdminRequest;
import ru.practicum.ewm.event.requestModel.EventPublicRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;

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

    List<EventFullDto> getAll(EventAdminRequest request, PageRequest pageRequest);

    EventFullDto moderate(long eventId, UpdateEventAdminRequest updateEventDto);

    EventFullDto getById(long id);

    List<EventShortDto> getAll(EventPublicRequest request,
                               String sort,
                               PageRequest pageRequest);
}
