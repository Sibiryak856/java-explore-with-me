package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;
import ru.practicum.ewm.event.validation.CheckEventDateNotEarlierSomeNHourLater;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEventAdminRequest extends UpdateEventBaseRequest {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CheckEventDateNotEarlierSomeNHourLater(parameter = "1",
            message = "EventDate must be no earlier than 1 hours from the current time")
    private LocalDateTime eventDate;

    private @Nullable StateAction stateAction;


    public boolean isStateNeedUpdate() {
        return stateAction != null;
    }

    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }
}
