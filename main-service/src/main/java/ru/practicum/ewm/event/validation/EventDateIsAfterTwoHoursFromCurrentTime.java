package ru.practicum.ewm.event.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = EventDateIsAfterTwoHoursFromCurrentTimeValidator.class)
@Target(ElementType.TYPE)
@Documented
public @interface EventDateIsAfterTwoHoursFromCurrentTime {

    String message() default "EventDate must be no earlier than 2 hours from the current time";

    Class<?>[] groups() default {};

    Class<? extends Payload> [] payload() default {};
}
