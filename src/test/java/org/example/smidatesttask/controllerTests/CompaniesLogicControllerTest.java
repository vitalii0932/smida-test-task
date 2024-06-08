package org.example.smidatesttask.controllerTests;

import org.example.smidatesttask.mapper.CompanyMapper;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.service.Violation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.FlashMap;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * CompaniesLogicController tests
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CompaniesLogicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private CompanyMapper companyMapper;

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
    }

    /**
     * tear down the test company
     */
    @After
    public void tearDown() {
        if (testCompany != null && testCompany.getId() != null && companyRepository.existsById(testCompany.getId())) {
            companyRepository.delete(testCompany);
        }
    }

    /**
     * test submit valid company to update
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testSubmitValidCompany_thenUpdate_thenAssertRedirectToCompanies() throws Exception {
        testCompany = companyRepository.save(testCompany);

        testCompany.setRegistrationNumber("new number");

        Long companiesCount = companyRepository.count();

        mockMvc.perform(post("/api/v1/companies/submit_company")
                        .flashAttr("company", companyMapper.toCompanyDTO(testCompany)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/companies"));

        Long newCompaniesCount = companyRepository.count();
        Company updatedCompany = companyRepository.findById(testCompany.getId()).orElse(null);

        assertNotNull(updatedCompany);
        assertEquals(testCompany, updatedCompany);
        assertEquals(companiesCount, newCompaniesCount);
    }

    /**
     * test submit invalid company to update
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testSubmitInvalidCompany_thenTryToUpdate_thenAssertRedirectToUpdateAndErrorMessage() throws Exception {
        testCompany = companyRepository.save(testCompany);

        testCompany.setRegistrationNumber("");

        ResultActions resultActions = mockMvc.perform(post("/api/v1/companies/submit_company")
                        .flashAttr("company", companyMapper.toCompanyDTO(testCompany)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/companies/update"));

        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();

        // assert the flash attribute contains the expected error message
        assertNotNull(flashMap.get("error"));
        List<Violation> violations = (List<Violation>) flashMap.get("error");
        assertEquals(violations.size(), 1);
        assertEquals(violations.get(0).getProperty(), "registrationNumber");
        assertEquals(violations.get(0).getMessage(), "Company registration number is required");
    }

    /**
     * test delete the existing company
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testDeleteExistingCompany_thenDelete_thenAssertRedirectToCompaniesAndCompaniesSizeMinusOne() throws Exception {
        testCompany = companyRepository.save(testCompany);

        Long companiesCount = companyRepository.count();

        mockMvc.perform(get("/api/v1/companies/delete/" + testCompany.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/companies"));

        Long newCompaniesCount = companyRepository.count();

        assertFalse(companyRepository.existsById(testCompany.getId()));
        assertEquals(companiesCount - 1, newCompaniesCount);
    }

    /**
     * test delete the non-existed company
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testDeleteNonExistedCompany_thenTryToDelete_thenAssertRedirectToCompaniesAndErrorMessage() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/companies/delete/" + UUID.randomUUID()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/companies"));

        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();

        assertNotNull(flashMap.get("error"));
        String errorMessage = flashMap.get("error").toString();
        assertEquals(errorMessage, "Company with this id not found");
    }
}
