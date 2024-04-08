package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.user.dto.UserShortDto;

import static ru.practicum.ewm.EwmApp.DATE_FORMAT;

@Data
@Builder
public class EventShortDto {


    private String annotation;

    private CategoryDto category;

    //private Integer confirmedRequests;

    @DateTimeFormat(pattern = DATE_FORMAT)
    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;
}
