package org.example.smidatesttask.serviceTests;

import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.models.Company;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.service.CompanyService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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

    /**
     * set up the test company
     */
    @Before
    public void setUp() {
        testCompany = new Company();
        testCompany.setAddress("test address");
        testCompany.setName("test name");
        testCompany.setRegistrationNumber("0123456789");
        testCompany.setCreatedAt(Timestamp.from(Instant.now()));

        testCompany = companyRepository.save(testCompany);
    }

    /**
     * tear down the test company
     */
    @After
    public void tearDown() {
        if (companyRepository.findById(testCompany.getId()).isPresent()) {
            companyRepository.delete(testCompany);
        }
    }

    /**
     * getAll function test
     */
    @Test
    public void company_getAll_thenListSizeMustBeNotEmpty() {
        List<Company> companies = companyService.getAll();

        assertNotNull(companies);
        assertFalse(companies.isEmpty());
    }
}
