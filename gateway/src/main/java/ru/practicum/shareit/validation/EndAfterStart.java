package ru.practicum.shareit.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CheckDateValidator.class)
@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EndAfterStart {
    String message() default "Время окончания броннирования не может быть раньше времени начала или равно ему.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
