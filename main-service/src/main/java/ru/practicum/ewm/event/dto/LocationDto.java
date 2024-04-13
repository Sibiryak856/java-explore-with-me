package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationDto {

    @NotNull
    @JsonProperty("lat")
    private Float latitude;

    @NotNull
    @JsonProperty("lon")
    private Float longitude;
}
