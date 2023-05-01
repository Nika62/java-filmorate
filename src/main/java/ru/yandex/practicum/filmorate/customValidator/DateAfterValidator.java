package ru.yandex.practicum.filmorate.customValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DateAfterValidator implements ConstraintValidator<DateAfter, LocalDate> {
    private String annotationDateAfter;

    @Override
    public void initialize(DateAfter date) {
        this.annotationDateAfter = date.value();
    }

    @Override
    public boolean isValid(LocalDate target, ConstraintValidatorContext context) {
        if (target != null) {
            return target.isAfter(LocalDate.parse(annotationDateAfter));
        }
        return false;
    }
}
