package ru.practicum.ewm.comment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentAdminRequestDto extends CommentRequestDto {

    private @Nullable StateAction stateAction;

    public boolean isStateNeedUpdate() {
        return stateAction != null;
    }

    public enum StateAction {
        PUBLISH_COMMENT,
        REJECT_COMMENT
    }
}
