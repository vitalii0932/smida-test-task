package org.example.smidatesttask.controllerTests;

import org.example.smidatesttask.dto.ReportDetailsDTO;
import org.example.smidatesttask.mapper.ReportDetailsMapper;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.model.ReportDetails;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportDetailsRepository;
import org.example.smidatesttask.repository.ReportRepository;
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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ReportDetailsLogicController tests
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ReportDetailsLogicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReportDetailsMapper reportDetailsMapper;

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

        // set up the test report details
        testReportDetails = new ReportDetails();
        testReportDetails.setReportId(testReport.getId());
        testReportDetails.setComments("test comments");
        testReportDetails.setFinancialData("{\"test\": \"json\"}");

        // set up the test report details dto
        testReportDetailsDTO = new ReportDetailsDTO();
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
     * test submit valid report details to update
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testSubmitValidReportDetails_thenUpdate_thenAssertRedirectToReportDetails() throws Exception {
        testReportDetails = reportDetailsRepository.save(testReportDetails);

        testReportDetailsDTO = reportDetailsMapper.toReportDetailsDTO(testReportDetails);

        testReportDetailsDTO.setComments("new comments");

        Long reportDetailsCount = reportDetailsRepository.count();

        mockMvc.perform(post("/api/v1/reports_details/submit_report_details")
                        .flashAttr("reportDetails", testReportDetailsDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/reports_details/" + testReportDetailsDTO.getReportId()));

        Long newReportDetailsCount = reportDetailsRepository.count();
        ReportDetails updatedReportDetails = reportDetailsRepository.findById(testReportDetails.getReportId()).orElse(null);

        assertNotNull(updatedReportDetails);
        assertEquals(updatedReportDetails, reportDetailsMapper.toReportDetails(testReportDetailsDTO));
        assertEquals(reportDetailsCount, newReportDetailsCount);
    }

    /**
     * test submit invalid report details to update
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testSubmitInvalidReportDetails_thenTryToUpdate_thenAssertRedirectToUpdateAndErrorMessage() throws Exception {
        testReportDetails = reportDetailsRepository.save(testReportDetails);

        testReportDetailsDTO = reportDetailsMapper.toReportDetailsDTO(testReportDetails);
        testReportDetailsDTO.setFinancialData("not json");

        ResultActions resultActions = mockMvc.perform(post("/api/v1/reports_details/submit_report_details")
                        .flashAttr("reportDetails", testReportDetailsDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/reports_details/update/" + testReportDetailsDTO.getReportId()));

        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();

        assertNotNull(flashMap.get("error"));
        String errorMessage = flashMap.get("error").toString();
        assertEquals(errorMessage, "This string isn't json");
    }

    /**
     * test delete the existing report details
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testDeleteExistingReportDetails_whenDelete_thenAssertRedirectToReportDetailsAndReportDetailsNotExist() throws Exception {
        testReportDetails = reportDetailsRepository.save(testReportDetails);

        mockMvc.perform(get("/api/v1/reports_details/delete/" + testReportDetails.getReportId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/reports_details/" + testReport.getId()));

        assertFalse(reportDetailsRepository.existsById(testReportDetails.getReportId()));
    }

    /**
     * test delete the non-existed report details
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testDeleteNonExistedReportDetails_whenDelete_thenAssertRedirectToReportDetailsAndErrorMessage() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/reports_details/delete/" + UUID.randomUUID()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/companies"));

        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();

        assertNotNull(flashMap.get("error"));
        String errorMessage = flashMap.get("error").toString();
        assertEquals(errorMessage, "Report details with this id not found");
    }
}
