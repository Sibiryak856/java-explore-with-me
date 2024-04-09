package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.user.dto.UserShortDto;

@Data
@Builder
public class EventFullDto {


    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private String publishedOn;

    private EventState state;

    private String title;

    private Long views;
}
