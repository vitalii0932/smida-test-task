package org.example.smidatesttask.serviceTests;

import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.service.CompanyService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CompanyService tests
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyRepository companyRepository;

    private Company testCompany;
    private CompanyDTO testCompanyDTO;

    /**
     * set up the test company
     */
    @Before
    public void setUp() {
        testCompanyDTO = new CompanyDTO();
        testCompanyDTO.setAddress("test address");
        testCompanyDTO.setName("test name");
        testCompanyDTO.setRegistrationNumber("0123456789");
        testCompanyDTO.setCreatedAt(Timestamp.from(Instant.now()));
    }

    /**
     * tear down the test company
     */
    @After
    public void tearDown() {
        if (testCompany != null && testCompany.getId() != null && companyRepository.findById(testCompany.getId()).isPresent()) {
            companyRepository.delete(testCompany);
        }
    }

    /**
     * getAll function test
     */
    @Test
    public void getAll_thenListSizeMustBeNotEmpty() {
        List<Company> companies = companyService.getAll();

        assertNotNull(companies);
        assertFalse(companies.isEmpty());
    }

    /**
     * save the valid company if company is invalid
     */
    @Test
    public void validCompany_whenSaved_thenCanBeFoundById() throws ValidationException {
        testCompany = companyService.save(testCompanyDTO);

        Company savedCompany = companyRepository.findById(testCompany.getId()).orElse(null);

        assertNotNull(savedCompany);
        assertEquals(testCompany, savedCompany);
    }

    /**
     * save the invalid company
     *
     * @throws ValidationException if company is invalid
     */
    @Test
    public void invalidCompany_whenTryToSave_thenAssertValidationException() throws ValidationException {
        testCompanyDTO.setName(""); // set invalid value

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            companyService.save(testCompanyDTO);
        });

        String actualMessage = exception.getViolations().toString();

        assertTrue(actualMessage.contains("property=name"));
        assertTrue(actualMessage.contains("message=Company name is required"));
    }
}
