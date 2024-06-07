package org.example.smidatesttask.controllerTests;

import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.model.ReportDetails;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportDetailsRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.example.smidatesttask.service.ReportDetailsService;
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
import java.sql.Timestamp;
import java.time.Instant;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

/**
 * CompaniesPageController functions test
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ReportDetailsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReportDetailsRepository reportDetailsRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private ReportDetails testReportDetails;
    private Report testReport;
    private Company testCompany;

    /**
     * set up the test reportDetails
     */
    @Before
    public void setUp() {
        // set up the test company
        testCompany = new Company();
        testCompany.setAddress("test address");
        testCompany.setName("test name");
        testCompany.setRegistrationNumber("0123456789");
        testCompany.setCreatedAt(Timestamp.from(Instant.now()));

        testCompany = companyRepository.save(testCompany);

        // set up the test report
        testReport = new Report();
        testReport.setReportDate(Timestamp.from(Instant.now()));
        testReport.setTotalRevenue(BigDecimal.ONE);
        testReport.setNetProfit(BigDecimal.ONE);
        testReport.setCompany(testCompany);

        testReport = reportRepository.save(testReport);

        //set up the test report details
        testReportDetails = new ReportDetails();
        testReportDetails.setReportId(testReport.getId());
        testReportDetails.setComments("test comments");
        testReportDetails.setFinancialData("{\"test\": \"json\"}");

        reportDetailsRepository.save(testReportDetails);
    }

    /**
     * tear down the test reportDetails
     */
    @After
    public void tearDown() {
        if (testReportDetails != null && testReportDetails.getReportId() != null && reportDetailsRepository.findById(testReportDetails.getReportId()).isPresent()) {
            reportDetailsRepository.delete(testReportDetails);
        }
        if (reportRepository.findById(testReport.getId()).isPresent()) {
            reportRepository.delete(testReport);
        }
        if (companyRepository.findById(testCompany.getId()).isPresent()) {
            companyRepository.delete(testCompany);
        }
    }

    /**
     * test reportDetails page controller
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadReportDetailsPage() throws Exception {
        mockMvc.perform(get("/api/v1/reports_details/" + testReport.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Report details</title>")))
                .andExpect(model().attributeExists("reportDetails"));
    }

    /**
     * test create reportDetails page controller
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadCreateReportDetailsPage() throws Exception {
        reportDetailsRepository.deleteById(testReportDetails.getReportId());

        mockMvc.perform(get("/api/v1/reports_details/create/" + testReport.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<span>Create</span>")))
                .andExpect(content().string(Matchers.not(containsString("<span>Update</span>"))));
    }

    /**
     * test update reportDetails page controller
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testLoadUpdateReportDetailsPage() throws Exception {
        mockMvc.perform(get("/api/v1/reports_details/update/" + testReportDetails.getReportId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<span>Update</span>")))
                .andExpect(content().string(Matchers.not(containsString("<span>Create</span>"))));
    }
}
