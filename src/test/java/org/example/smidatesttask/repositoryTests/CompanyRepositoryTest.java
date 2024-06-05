package org.example.smidatesttask.repositoryTests;

import org.example.smidatesttask.models.Company;
import org.example.smidatesttask.repository.CompanyRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CompanyRepository tests
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class CompanyRepositoryTest {

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
     * create test
     */
    @Test
    public void company_whenSaved_thenCanBeFoundById() {
        Company savedCompany = companyRepository.findById(testCompany.getId()).orElse(null);

        assertNotNull(savedCompany);
        assertEquals(testCompany, savedCompany);
    }

    /**
     * update test
     */
    @Test
    public void company_whenUpdated_thenCanBeFoundById() {
        testCompany.setName("updated name");
        companyRepository.save(testCompany);

        Optional<Company> updatedCompany = companyRepository.findById(testCompany.getId());

        assertTrue(updatedCompany.isPresent());
        assertEquals("updated name", updatedCompany.get().getName());
    }

    /**
     * delete test
     */
    @Test
    public void company_whenDeleted_thenCannotBeFoundById() {
        companyRepository.delete(testCompany);

        assertFalse(companyRepository.findById(testCompany.getId()).isPresent());
    }
}
