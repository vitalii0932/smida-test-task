package org.example.smidatesttask.serviceTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.smidatesttask.dto.ReportDetailsDTO;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.model.ReportDetails;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportDetailsRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.example.smidatesttask.service.ReportDetailsService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * ReportService tests
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class ReportDetailsServiceTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportDetailsService reportDetailsService;

    @Autowired
    private ReportDetailsRepository reportDetailsRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private ReportDetails testReportDetails;
    private Report testReport;
    private Company testCompany;
    private ReportDetailsDTO testReportDetailsDTO;

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
        testReportDetailsDTO = new ReportDetailsDTO();
        testReportDetailsDTO.setReportId(testReport.getId());
        testReportDetailsDTO.setComments("test comments");

        String testJsonString = "{\"test\": \"json\"}";
        try {
            JsonNode testJsonNode = objectMapper.readTree(testJsonString);
            testReportDetailsDTO.setFinancialData(testJsonNode.asText());
        } catch (Exception e) {
            e.fillInStackTrace();
        }
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
}
