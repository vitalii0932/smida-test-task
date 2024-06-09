package org.example.smidatesttask.controllerTests;

import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.hamcrest.Matchers;
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

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ReportsPageController functions test
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ReportsPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Company testCompany;
    private Report testReport;

    /**
     * set up the test report
     */
    @Before
    public void setUp() {
        // set up the test company
        testCompany = new Company();
        testCompany.setName("test name");
        testCompany.setAddress("test address");
        testCompany.setRegistrationNumber("0123456789");

        testCompany = companyRepository.save(testCompany);
        
        // set up the test report
        testReport = new Report();
        testReport.setCompany(testCompany);
        testReport.setNetProfit(BigDecimal.ONE);
        testReport.setTotalRevenue(BigDecimal.ONE);

        testReport = reportRepository.save(testReport);
    }

    /**
     * tear down the test report
     */
    @After
    public void tearDown() {
        if (testReport != null && testReport.getId() != null && reportRepository.existsById(testReport.getId())) {
            reportRepository.delete(testReport);
        }
        if (testCompany != null && testCompany.getId() != null && companyRepository.existsById(testCompany.getId())) {
            companyRepository.delete(testCompany);
        }
    }

    /**
     * test reports page controller
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadReportsPage() throws Exception {
        mockMvc.perform(get("/api/v1/reports/" + testCompany.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Reports</title>")))
                .andExpect(model().attributeExists("reports"));
    }

    /**
     * test create report page controller
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadCreateReportPage() throws Exception {
        mockMvc.perform(get("/api/v1/reports/create/" + testCompany.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<span>Create</span>")))
                .andExpect(content().string(Matchers.not(containsString("<span>Update</span>"))));
    }

    /**
     * test update report page controller
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadUpdateReportPage() throws Exception {
        mockMvc.perform(get("/api/v1/reports/update/" + testReport.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<span>Update</span>")))
                .andExpect(content().string(Matchers.not(containsString("<span>Create</span>"))));
    }
}
