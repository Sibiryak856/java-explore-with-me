package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.EventUpdateDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface EventService {

    EventFullDto save(long userId, NewEventDto eventDto);

    List<EventShortDto> getAllByUserId(long userId, PageRequest pageRequest);

    EventFullDto getByEventId(long userId, long eventId);

    EventFullDto update(long userId, long eventId, EventUpdateDto updateEventDto);

    List<RequestDto> getRequestByEventId(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(long userId,
                                                        long eventId,
                                                        EventRequestStatusUpdateRequest request);
}
