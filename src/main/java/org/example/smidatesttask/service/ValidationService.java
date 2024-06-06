package org.example.smidatesttask.service;

import org.example.smidatesttask.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * service for validate entities
 */
@Service
@RequiredArgsConstructor
public class ValidationService {

    private final Validator validator;

    /**
     * check does entity is valid
     *
     * @param t - entity
     * @param <T> - entity class
     * @throws ValidationException if entity not valid
     */
    public <T> void isValid(T t) throws ValidationException {
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(t);

        if (!constraintViolations.isEmpty()) {
            throw new ValidationException(buildViolationsList(constraintViolations));
        }
    }

    /**
     * buildViolationsList function
     *
     * @param constraintViolations - constraintViolations from validation
     * @param <T>                  - type
     * @return a list of Violation
     */
    private <T> List<Violation> buildViolationsList(Set<ConstraintViolation<T>> constraintViolations) {
        return constraintViolations.stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .collect(Collectors.toList());
    }
}