package org.example.smidatesttask.controllerTests;

import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.repository.CompanyRepository;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CompaniesPageController functions test
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class CompaniesPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    private Company testCompany;

    /**
     * set up the test company
     */
    @Before
    public void setUp() {
        testCompany = new Company();
        testCompany.setName("test name");
        testCompany.setAddress("test address");
        testCompany.setRegistrationNumber("0123456789");

        testCompany = companyRepository.save(testCompany);
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
     * test companies page controller
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadCompaniesPage() throws Exception {
        mockMvc.perform(get("/api/v1/companies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Companies</title>")))
                .andExpect(model().attributeExists("companies"));
    }

    /**
     * test create company page controller
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadCreateCompanyPage() throws Exception {
        mockMvc.perform(get("/api/v1/companies/create"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<span>Create</span>")))
                .andExpect(content().string(Matchers.not(containsString("<span>Update</span>"))));
    }

    /**
     * test update company page controller
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadUpdateCompanyPage() throws Exception {
        mockMvc.perform(get("/api/v1/companies/update/" + testCompany.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<span>Update</span>")))
                .andExpect(content().string(Matchers.not(containsString("<span>Create</span>"))));
    }
}
