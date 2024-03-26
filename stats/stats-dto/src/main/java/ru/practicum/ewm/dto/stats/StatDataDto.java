package ru.practicum.ewm.dto.stats;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDataDto {

    @JsonProperty("app")
    private String appName;

    private String uri;

    private Long hits;

}
