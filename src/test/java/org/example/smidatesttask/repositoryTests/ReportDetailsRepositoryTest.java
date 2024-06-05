package org.example.smidatesttask.repositoryTests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.smidatesttask.models.Company;
import org.example.smidatesttask.models.Report;
import org.example.smidatesttask.models.ReportDetails;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportDetailsRepository;
import org.example.smidatesttask.repository.ReportRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReportDetailsRepository tests
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class ReportDetailsRepositoryTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportDetailsRepository reportDetailsRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ReportRepository reportRepository;

    private Company testCompany;

    private Report testReport;

    private ReportDetails testReportDetails;

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
        testReport.setTotalRevenue(1.0);
        testReport.setNetProfit(1.0);
        testReport.setCompany(testCompany);

        testReport = reportRepository.save(testReport);

        //set up the test report details
        testReportDetails = new ReportDetails();
        testReportDetails.setReportId(testReport.getId());
        testReportDetails.setComments("test comments");

        String testJsonString = "{\"nick\": \"cowtowncoder\"}";
        try {
            JsonNode testJsonNode = objectMapper.readTree(testJsonString);
            testReportDetails.setFinancialData(testJsonNode.asText());
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        testReportDetails = reportDetailsRepository.save(testReportDetails);
    }

    /**
     * tear down the test reportDetails
     */
    @After
    public void tearDown() {
        if (reportDetailsRepository.findById(testReportDetails.getReportId()).isPresent()) {
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
     * create test
     */
    @Test
    public void reportDetails_whenSaved_thenCanBeFoundById() {
        ReportDetails savedReportDetails = reportDetailsRepository.findById(testReportDetails.getReportId()).orElse(null);

        assertNotNull(savedReportDetails);
        assertEquals(testReportDetails, savedReportDetails);
    }

    /**
     * update test
     */
    @Test
    public void reportDetails_whenUpdated_thenCanBeFoundById() {
        testReportDetails.setComments("new comments");

        reportDetailsRepository.save(testReportDetails);

        Optional<ReportDetails> updatedReportDetails = reportDetailsRepository.findById(testReportDetails.getReportId());

        assertTrue(updatedReportDetails.isPresent());
        assertEquals("new comments", updatedReportDetails.get().getComments());
    }

    /**
     * delete test
     */
    @Test
    public void reportDetails_whenDeleted_thenCannotBeFoundById() {
        reportDetailsRepository.delete(testReportDetails);

        assertFalse(reportDetailsRepository.findById(testReportDetails.getReportId()).isPresent());
    }
}
