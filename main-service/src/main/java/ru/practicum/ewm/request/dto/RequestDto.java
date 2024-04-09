package ru.practicum.ewm.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.request.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class RequestDto {

    private LocalDateTime created;

    @JsonProperty("event")
    private Long eventId;

    private Long id;

    @JsonProperty("requester")
    private Long requesterId;

    private RequestStatus status;
}
