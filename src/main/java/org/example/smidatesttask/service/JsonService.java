package org.example.smidatesttask.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * service for validate json
 */
@Service
public class JsonService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * parse str to json
     *
     * @param str - str from user
     * @return - json
     * @throws RuntimeException if str isn't json
     */
    public String strToJsonNode(String str) throws RuntimeException {
        try {
            return objectMapper.readTree(str).toString();
        } catch (Exception e) {
            throw new RuntimeException("This string isn't json");
        }
    }
}
