package org.example.smidatesttask.service;

import lombok.Data;

/**
 * violation record
 */
@Data
public final class Violation {
    private final String property;
    private final String message;
}

