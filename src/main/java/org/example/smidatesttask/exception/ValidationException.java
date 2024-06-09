package org.example.smidatesttask.exception;

import org.example.smidatesttask.service.Violation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * validation exception class
 */
@Getter
@RequiredArgsConstructor
public class ValidationException extends Exception {

    private final List<Violation> violations;
}
