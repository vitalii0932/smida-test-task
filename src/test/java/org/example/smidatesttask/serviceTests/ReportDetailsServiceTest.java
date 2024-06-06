package org.example.smidatesttask.serviceTests;

import org.example.smidatesttask.dto.ReportDetailsDTO;
import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.mapper.ReportDetailsMapper;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.model.ReportDetails;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportDetailsRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.example.smidatesttask.service.ReportDetailsService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReportService tests
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class ReportDetailsServiceTest {

    @Autowired
    private ReportDetailsMapper reportDetailsMapper;

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
        testReportDetailsDTO.setFinancialData("{\"test\": \"json\"}");
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
     * get report details with correct report id
     */
    @Test
    public void correctReportDetails_getReportDetails_thenReturnCorrectReportDetails() {
        try {
            testReportDetails = reportDetailsService.save(testReportDetailsDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }

        assertNotNull(testReportDetails);
        assertEquals(testReportDetails.getFinancialData(), testReportDetailsDTO.getFinancialData());
        assertEquals(testReportDetails.getComments(), testReportDetailsDTO.getComments());
    }

    /**
     * get report details with incorrect report id
     */
    @Test
    public void incorrectReportDetails_getReportDetails_thenReturnCorrectReportDetails() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportDetailsService.getReportDetails(UUID.randomUUID());
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Report details with this id not found");
    }

    /**
     * save the valid report details test
     */
    @Test
    public void validReportDetails_whenSaved_thenCanBeFoundById() {
        try {
            testReportDetails = reportDetailsService.save(testReportDetailsDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }

        ReportDetails savedReportDetails = reportDetailsRepository.findById(testReportDetails.getReportId()).orElse(null);

        assertNotNull(savedReportDetails);
        assertEquals(testReportDetails, savedReportDetails);
    }

    /**
     * save the invalid report details with incorrect id test
     */
    @Test
    public void invalidReportDetailsWithIncorrectId_whenTryToSave_thenAssertRuntimeException() {
        testReportDetailsDTO.setReportId(UUID.randomUUID());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportDetailsService.save(testReportDetailsDTO);
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Report with this id was not found");
    }

    /**
     * save the invalid report details with taken id test
     */
    @Test
    public void invalidReportDetailsWithTakenId_whenTryToSave_thenAssertRuntimeException() {
        try {
            testReportDetails = reportDetailsService.save(testReportDetailsDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }

        testReportDetailsDTO.setReportId(testReportDetails.getReportId());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportDetailsService.save(testReportDetailsDTO);
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Report with this id is already taken");
    }

    /**
     * save the invalid report details with not correct financial data test
     */
    @Test
    public void invalidReportDetailsWithNotCorrectFinancialData_whenTryToSave_thenAssertRuntimeException() {
        testReportDetailsDTO.setFinancialData("incorrect json");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportDetailsService.save(testReportDetailsDTO);
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "This string isn't json");
    }

    /**
     * updated the valid report details test
     */
    @Test
    public void validReportDetails_whenUpdated_thenCanBeFoundById() {
        try {
            testReportDetails = reportDetailsService.save(testReportDetailsDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }

        testReportDetailsDTO.setReportId(testReportDetails.getReportId());
        testReportDetailsDTO.setComments("new comment");

        ReportDetails updatedReportDetails;
        try {
            updatedReportDetails = reportDetailsService.update(testReportDetailsDTO);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        assertEquals(updatedReportDetails, reportDetailsMapper.toReportDetails(testReportDetailsDTO));
    }

    /**
     * update the invalid report details with incorrect id test
     */
    @Test
    public void invalidReportDetailsWithIncorrectId_whenTryToUpdate_thenAssertRuntimeException() {
        try {
            testReportDetails = reportDetailsService.save(testReportDetailsDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }

        testReportDetailsDTO.setReportId(UUID.randomUUID());
        testReportDetailsDTO.setComments("new comment");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportDetailsService.update(testReportDetailsDTO);
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Report details with this id not found");
    }

    /**
     * update the invalid report details with not correct financial data test
     */
    @Test
    public void invalidReportDetailsWithNotCorrectFinancialData_whenTryToUpdate_thenAssertRuntimeException() {
        try {
            testReportDetails = reportDetailsService.save(testReportDetailsDTO);
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }

        testReportDetailsDTO.setReportId(testReportDetails.getReportId());
        testReportDetailsDTO.setFinancialData("incorrect json");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportDetailsService.update(testReportDetailsDTO);
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "This string isn't json");
    }

    /**
     * delete the existing report details test
     */
    @Test
    public void existingReportDetails_whenDeleted_thenCannotBeFoundById() {
        try {
            testReportDetails = reportDetailsService.save(testReportDetailsDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            reportDetailsService.delete(testReportDetails.getReportId());
        } catch (Exception e) {
        }

        assertFalse(reportDetailsRepository.existsById(testReportDetails.getReportId()));
    }

    /**
     * delete the non-existent report details test
     */
    @Test
    public void nonexistentReport_whenTryToDelete_thenAssertRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportDetailsService.delete(UUID.randomUUID());
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Report details with this id not found");
    }
}
