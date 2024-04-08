package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class LocationDto {

    @NotNull
    @JsonProperty("lat")
    private Float latitude;

    @NotNull
    @JsonProperty("lon")
    private Float longitude;
}
