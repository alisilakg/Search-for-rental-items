package ru.practicum.shareit.validation;

import ru.practicum.shareit.booking.dto.BookingItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class CheckDateValidator implements ConstraintValidator<EndAfterStart, BookingItemRequestDto> {

    @Override
    public void initialize(EndAfterStart constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingItemRequestDto bookingDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();
        if (start == null || end == null) {
            return false;
        }
        if (end.isEqual(start)) {
            return false;
        }
        return end.isAfter(start);

    }
}

