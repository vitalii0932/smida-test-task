package org.example.smidatesttask.serviceTests;

import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.service.JsonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JsonService tests
 */
@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class JsonServiceTest {

    @Autowired
    private JsonService jsonService;

    /**
     * parse the valid string test
     */
    @Test
    public void validStr_whenToJsonNode_returnStr() {
        String resultJson;

        try {
            resultJson = jsonService.strToJsonNode("{\"test\": \"json\"}");
        } catch (Exception e) {
            throw new RuntimeException();
        }

        System.out.println(resultJson);

        assertEquals(resultJson, "{\"test\":\"json\"}");
    }

    /**
     * parse the invalid string test
     */
    @Test
    public void invalidStr_whenToJsonNode_thrownRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jsonService.strToJsonNode("invalid json");
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "This string isn't json");
    }
}
