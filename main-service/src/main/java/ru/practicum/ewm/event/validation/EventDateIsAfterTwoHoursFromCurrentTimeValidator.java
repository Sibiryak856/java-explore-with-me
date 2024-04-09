package ru.practicum.ewm.event.validation;

import ru.practicum.ewm.event.dto.NewEventDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.time.LocalDateTime;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class EventDateIsAfterTwoHoursFromCurrentTimeValidator implements
        ConstraintValidator<EventDateIsAfterTwoHoursFromCurrentTime, NewEventDto> {

    @Override
    public boolean isValid(NewEventDto eventDto, ConstraintValidatorContext context) {
        if (eventDto.getEventDate() == null) {
            return false;
        }
        if (!(eventDto.getEventDate() instanceof LocalDateTime)) {
            throw new IllegalArgumentException(
                    "Illegal method signature, expected parameter of type LocalDateTime.");
        }

        return (eventDto.getEventDate().isBefore(
                LocalDateTime.now().plusHours(2)));
    }
}
