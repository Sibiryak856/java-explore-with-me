package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.event.validation.EventDateIsAfterTwoHoursFromCurrentTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Data
@Builder
@EventDateIsAfterTwoHoursFromCurrentTime
public class NewEventDto {

    @NotBlank
    @Size(min = 20, message = "{validation.name.size.too_short}")
    @Size(max = 2000, message = "{validation.name.size.too_long}")
    private String annotation;

    @NotNull
    @JsonProperty("category")
    private Long categoryId;

    @NotBlank
    @Size(min = 20, message = "{validation.name.size.too_short}")
    @Size(max = 7000, message = "{validation.name.size.too_long}")
    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    @Builder.Default
    private Boolean paid = FALSE;

    @Builder.Default
    private Integer participantLimit = 0;

    @Builder.Default
    private Boolean requestModeration = TRUE;

    @NotBlank
    @Size(min = 3, message = "{validation.name.size.too_short}")
    @Size(max = 120, message = "{validation.name.size.too_long}")
    private String title;
}
