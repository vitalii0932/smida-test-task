package org.example.smidatesttask.controllerTests;

import org.example.smidatesttask.dto.ReportDTO;
import org.example.smidatesttask.mapper.ReportMapper;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.repository.CompanyRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * ReportsLogicControllerTest tests
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ReportsLogicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ReportMapper reportMapper;

    private Report testReport;
    private ReportDTO testReportDTO;
    private Company testCompany;

    /**
     * set up the test report
     */
    @Before
    public void setUp() {
        // set up the test company
        testCompany = new Company();
        testCompany.setAddress("test address");
        testCompany.setRegistrationNumber("0123456789");
        testCompany.setName("test name");

        testCompany = companyRepository.save(testCompany);

        // set up the test report
        testReport = new Report();
        testReport.setNetProfit(BigDecimal.ONE);
        testReport.setTotalRevenue(BigDecimal.ONE);
        testReport.setCompany(testCompany);

        // set up the test report dto
        testReportDTO = new ReportDTO();
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
     * test submit valid report to update
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testSubmitValidReport_thenUpdate_thenAssertRedirectToReports() throws Exception {
        testReport = reportRepository.save(testReport);

        testReportDTO = reportMapper.toReportDTO(testReport);
        testReportDTO.setCompanyId(testCompany.getId());

        testReportDTO.setTotalRevenue(BigDecimal.TEN);

        Long reportsCount = reportRepository.count();

        mockMvc.perform(post("/api/v1/reports/submit_report")
                        .flashAttr("report", testReportDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/reports/" + testCompany.getId()));

        Long newReportsCount = reportRepository.count();
        Report updatedReport = reportRepository.findById(testReport.getId()).orElse(null);

        assertNotNull(updatedReport);
        assertEquals(testReportDTO.getId(), updatedReport.getId());
        assertEquals(testReportDTO.getCompanyId(), updatedReport.getCompany().getId());
        assertEquals(testReportDTO.getTotalRevenue().compareTo(updatedReport.getTotalRevenue()), 0);
        assertEquals(testReportDTO.getNetProfit().compareTo(updatedReport.getNetProfit()), 0);
        assertEquals(reportsCount, newReportsCount);
    }

    /**
     * test submit invalid report to update
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testSubmitInvalidCompany_thenTryToUpdate_thenAssertRedirectToUpdateAndErrorMessage() throws Exception {
        testReport = reportRepository.save(testReport);

        testReportDTO = reportMapper.toReportDTO(testReport);
        testReportDTO.setCompanyId(null);

        ResultActions resultActions = mockMvc.perform(post("/api/v1/reports/submit_report")
                        .flashAttr("report", testReportDTO))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/reports/update/" + testReport.getId()));

        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();

        assertNotNull(flashMap.get("error"));
        String errorMessage = flashMap.get("error").toString();
        assertEquals(errorMessage, "Company id is required");
    }

    /**
     * test delete the existing report
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testDeleteExistingReport_whenDelete_thenAssertRedirectToReportsAndReportsSizeMinusOne() throws Exception {
        testReport = reportRepository.save(testReport);

        Long reportsCount = reportRepository.count();

        mockMvc.perform(get("/api/v1/reports/delete/" + testReport.getId()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/reports/" + testCompany.getId()));

        Long newReportsCount = reportRepository.count();

        assertFalse(reportRepository.existsById(testReport.getId()));
        assertEquals(reportsCount - 1, newReportsCount);
    }

    /**
     * test delete the non-existed report
     *
     * @throws Exception if something wrong
     */
    @Test
    public void testDeleteNonExistedReport_whenDelete_thenAssertRedirectToReportsAndErrorMessage() throws Exception {
        ResultActions resultActions = mockMvc.perform(get("/api/v1/reports/delete/" + UUID.randomUUID()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/api/v1/companies"));

        MvcResult result = resultActions.andReturn();
        FlashMap flashMap = result.getFlashMap();

        assertNotNull(flashMap.get("error"));
        String errorMessage = flashMap.get("error").toString();
        assertEquals(errorMessage, "Report with this id not found");
    }
}
