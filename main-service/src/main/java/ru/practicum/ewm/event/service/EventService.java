package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.NewEventDto;

public interface EventService {

    EventFullDto save(long userId, NewEventDto eventDto);
}
