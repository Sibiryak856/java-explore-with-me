package ru.practicum.ewm.event.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Constraint(validatedBy = CheckEventDateNotEarlierSomeNHourLaterValidator.class)
@Target(ElementType.FIELD)
@Documented
public @interface CheckEventDateNotEarlierSomeNHourLater {

    String message() default "EventDate must be no earlier than 2 hours from the current time";

    String parameter() default "2";

    Class<?>[] groups() default {};

    Class<? extends Payload> [] payload() default {};
}
