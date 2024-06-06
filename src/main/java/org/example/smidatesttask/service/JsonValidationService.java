package org.example.smidatesttask.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * service for validate json
 */
@Service
public class JsonValidationService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * check does the str is json
     *
     * @param str - str from user
     * @throws RuntimeException if str isn't json
     */
    public static void isValidJSON(String str) throws RuntimeException {
        try {
            objectMapper.readTree(str);
        } catch (Exception e) {
            throw new RuntimeException("This string isn't json");
        }
    }
}
