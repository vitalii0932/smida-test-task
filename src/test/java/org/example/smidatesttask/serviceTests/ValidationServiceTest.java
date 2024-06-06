package org.example.smidatesttask.serviceTests;

import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.service.ValidationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ValidationService tests
 */
@SpringBootTest
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
public class ValidationServiceTest {

    @Autowired
    private ValidationService validationService;

    /**
     * test validation of the valid company
     */
    @Test
    public void validCompany_tryToValidate_thenAssertNotThrowsValidationException() {
        Company validCompany = new Company();
        validCompany.setName("company");
        validCompany.setAddress("address");
        validCompany.setRegistrationNumber("0123456789");

        assertDoesNotThrow(() -> validationService.isValid(validCompany));
    }

    /**
     * test validation of the invalid company
     */
    @Test
    public void invalidCompany_tryToValidate_thenAssertThrowsValidationException() {
        Company invalidCompany = new Company();
        invalidCompany.setName(null); // set invalid data
        invalidCompany.setAddress(""); // set invalid data
        invalidCompany.setRegistrationNumber(null); // set invalid data

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.isValid(invalidCompany);
        });

        String actualMessage = exception.getViolations().toString();

        assertTrue(actualMessage.contains("property=name"));
        assertTrue(actualMessage.contains("message=Company name is required"));

        assertTrue(actualMessage.contains("property=registrationNumber"));
        assertTrue(actualMessage.contains("message=Company registration number is required"));

        assertTrue(actualMessage.contains("property=address"));
        assertTrue(actualMessage.contains("message=Company address is required"));
    }

    /**
     * test validation of the valid report
     */
    @Test
    public void validReport_tryToValidate_thenAssertNotThrowsValidationException() {
        Company validCompany = new Company();
        validCompany.setName("company");
        validCompany.setAddress("address");
        validCompany.setRegistrationNumber("0123456789");

        Report validReport = new Report();
        validReport.setCompany(validCompany);

        assertDoesNotThrow(() -> validationService.isValid(validReport));
    }

    /**
     * test validation of the invalid report
     */
    @Test
    public void invalidReport_tryToValidate_thenAssertThrowsValidationException() {
        Report invalidReport = new Report();
        invalidReport.setCompany(null);

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            validationService.isValid(invalidReport);
        });

        String actualMessage = exception.getViolations().toString();

        assertTrue(actualMessage.contains("property=company"));
        assertTrue(actualMessage.contains("message=Company is required"));
    }
}
