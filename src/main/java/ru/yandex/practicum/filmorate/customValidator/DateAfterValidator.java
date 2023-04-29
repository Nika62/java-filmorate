package ru.yandex.practicum.filmorate.customValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateAfterValidator implements ConstraintValidator<DateAfter, LocalDate> {

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date != null) {
            return date.isAfter(LocalDate.of(1895, 12, 28));
        }
        return false;
    }
}
