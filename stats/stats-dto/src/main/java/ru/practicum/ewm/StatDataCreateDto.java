package ru.practicum.ewm;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatDataCreateDto {

    @NotBlank
    @JsonProperty("app")
    private String appName;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @NotNull
    @JsonProperty("timestamp")
    private String created;
}
