package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

@Data
//@Builder
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEventAdminRequest extends UpdateEventBaseRequest {

    private @Nullable StateAction stateAction;

    @JsonIgnore
    public boolean isStateNeedUpdate() {
        return stateAction != null;
    }

    public enum StateAction {
        PUBLISH_EVENT,
        REJECT_EVENT
    }
}