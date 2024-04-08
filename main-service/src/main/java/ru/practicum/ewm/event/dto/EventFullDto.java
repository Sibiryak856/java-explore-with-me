package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.user.dto.UserShortDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static ru.practicum.ewm.EwmApp.DATE_FORMAT;

@Data
@Builder
public class EventFullDto {


    private String annotation;

    private CategoryDto category;

    //private Integer confirmedRequests;

    @DateTimeFormat(pattern = DATE_FORMAT)
    private String createdOn;

    private String description;

    @DateTimeFormat(pattern = DATE_FORMAT)
    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    @DateTimeFormat(pattern = DATE_FORMAT)
    private String publishedOn;

    private EventState state;

    private String title;

    private Long views;
}
