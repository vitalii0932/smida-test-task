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
import java.util.UUID;

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
        if (testCompany != null && testCompany.getId() != null && companyRepository.existsById(testCompany.getId())) {
            companyRepository.deleteById(testCompany.getId());
        }
    }

    /**
     * getAll function test
     */
    @Test
    public void getAll_thenListSizeMustBeNotEmpty() {
        try {
            companyService.save(testCompanyDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Company> companies = companyService.getAll();

        assertNotNull(companies);
        assertFalse(companies.isEmpty());
    }

    /**
     * find the existing company test
     */
    @Test
    public void existingCompany_whenTryToFind_returnCompany() {
        Company findedCompany;

        try {
            testCompany = companyService.save(testCompanyDTO);

            findedCompany = companyService.findCompanyById(testCompany.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(testCompany, findedCompany);
    }

    /**
     * find the non-existent company test
     */
    @Test
    public void nonexistentCompany_whenTryToFind_thenAssertRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.findCompanyById(UUID.randomUUID());
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Company with this id not found");
    }

    /**
     * find the existing company dto test
     */
    @Test
    public void existingCompany_whenTtyToFindDTO_returnCompanyDTO() {
        CompanyDTO findedCompanyDTO;

        try {
            testCompany = companyService.save(testCompanyDTO);

            testCompanyDTO.setId(testCompany.getId());

            findedCompanyDTO = companyService.findCompanyDTOById(testCompany.getId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(testCompanyDTO, findedCompanyDTO);
    }

    /**
     * find the non-existent company dto test
     */
    @Test
    public void nonexistentCompany_whenTryToFindDTO_thenAssertRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.findCompanyDTOById(UUID.randomUUID());
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Company with this id not found");
    }

    /**
     * create a new company test
     */
    @Test
    public void createCompany_whenCreateOrUpdate_createANewCompany() {
        try {
            testCompany = companyService.createOrUpdateCompany(testCompanyDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Company savedCompany = companyRepository.findById(testCompany.getId()).orElse(null);

        assertNotNull(savedCompany);
        assertEquals(testCompany, savedCompany);
    }

    /**
     * update an existing company test
     */
    @Test
    public void updateCompany_whenCreateOrUpdate_updateCompany() {
        try {
            testCompany = companyService.save(testCompanyDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        testCompanyDTO.setId(testCompany.getId());
        testCompanyDTO.setName("new test company name");

        Company updatedCompany;
        try {
            updatedCompany = companyService.createOrUpdateCompany(testCompanyDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(updatedCompany.getName(), testCompanyDTO.getName());
    }


    /**
     * save the valid company test
     */
    @Test
    public void validCompany_whenSaved_thenCanBeFoundById() {
        try {
            testCompany = companyService.save(testCompanyDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        Company savedCompany = companyRepository.findById(testCompany.getId()).orElse(null);

        assertNotNull(savedCompany);
        assertEquals(testCompany, savedCompany);
    }

    /**
     * save the invalid company test
     */
    @Test
    public void invalidCompany_whenTryToSave_thenAssertValidationException() {
        testCompanyDTO.setName(""); // set invalid value

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            companyService.save(testCompanyDTO);
        });

        String actualMessage = exception.getViolations().toString();

        assertTrue(actualMessage.contains("property=name"));
        assertTrue(actualMessage.contains("message=Company name is required"));
    }

    /**
     * update the valid company test
     */
    @Test
    public void validCompany_whenUpdated_thenCanBeFoundById() {
        try {
            testCompany = companyService.save(testCompanyDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        testCompanyDTO.setId(testCompany.getId());
        testCompanyDTO.setName("new test company name");

        Company updatedCompany;
        try {
            updatedCompany = companyService.update(testCompanyDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertEquals(updatedCompany.getName(), testCompanyDTO.getName());
    }

    /**
     * update the invalid company test
     */
    @Test
    public void invalidCompany_whenTryToUpdate_thenAssertValidationException() {
        try {
            testCompany = companyService.save(testCompanyDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        testCompanyDTO.setId(testCompany.getId());
        testCompanyDTO.setAddress(""); // set invalid value

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            companyService.save(testCompanyDTO);
        });

        String actualMessage = exception.getViolations().toString();

        assertTrue(actualMessage.contains("property=address"));
        assertTrue(actualMessage.contains("message=Company address is required"));
    }

    /**
     * delete the existing company test
     */
    @Test
    public void existingCompany_whenDeleted_thenCannotBeFoundById() {
        try {
            testCompany = companyService.save(testCompanyDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        try {
            companyService.delete(testCompany.getId());
        } catch (Exception e) {
            throw new RuntimeException();
        }

        assertFalse(companyRepository.existsById(testCompany.getId()));
    }

    /**
     * delete the non-existent company test
     */
    @Test
    public void nonexistentCompany_whenTryToDelete_thenAssertRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            companyService.delete(UUID.randomUUID());
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Company with this id not found");
    }
}
