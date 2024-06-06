package org.example.smidatesttask.serviceTests;

import org.example.smidatesttask.dto.ReportDTO;
import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.example.smidatesttask.service.ReportService;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReportService tests
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private CompanyRepository companyRepository;

    private Report testReport;
    private Company testCompany;
    private ReportDTO testReportDTO;

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

        // set up the test report dto
        testReportDTO = new ReportDTO();
        testReportDTO.setNetProfit(BigDecimal.ONE);
        testReportDTO.setTotalRevenue(BigDecimal.ONE);
        testReportDTO.setCompanyId(testCompany.getId());
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
     * getAll function test
     */
    @Test
    public void getAllByCompany_thenListSizeMustBeNotEmptyAndCompanyMustBeCorrect() {
        try {
            testReport = reportService.save(testReportDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        List<Report> reports = reportService.getAllReportByCompany(testReportDTO.getCompanyId());

        assertNotNull(reports);
        assertFalse(reports.isEmpty());

        for (Report report : reports) {
            assertEquals(report.getCompany().getId(), testReportDTO.getCompanyId());
        }
    }

    /**
     * findReportById function test
     */
    @Test
    public void findReportById_thenGetReport() {
        try {
            testReport = reportService.save(testReportDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        Report reportFromDb;
        try {
            reportFromDb = reportService.findReportById(testReport.getId());
        } catch (Exception e) {
            throw new RuntimeException();
        }

        assertNotNull(reportFromDb);
        assertEquals(testReport, reportFromDb);
    }

    /**
     * findReportDTOById function test
     */
    @Test
    public void findReportDTOById_thenGetReport() {
        try {
            testReport = reportService.save(testReportDTO);

            testReportDTO.setId(testReport.getId());
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        ReportDTO reportDTOFromDb;
        try {
            reportDTOFromDb = reportService.findReportDTOById(testReport.getId());
        } catch (Exception e) {
            throw new RuntimeException();
        }

        assertNotNull(reportDTOFromDb);
        assertEquals(testReportDTO, reportDTOFromDb);
    }

    /**
     * create a new report test
     */
    @Test
    public void createReport_whenCreateOrUpdate_createANewReport() {
        try {
            testReport = reportService.createOrUpdateReport(testReportDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Report savedReport = reportRepository.findById(testReport.getId()).orElse(null);

        assertNotNull(savedReport);
        assertEquals(testReport, savedReport);
    }

    /**
     * update an existing report test
     */
    @Test
    public void updateReport_whenCreateOrUpdate_updateReport() {
        try {
            testReport = reportService.save(testReportDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        testReportDTO.setId(testReport.getId());
        testReportDTO.setTotalRevenue(BigDecimal.TEN);

        Report updatedReport;
        try {
            updatedReport = reportService.createOrUpdateReport(testReportDTO);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        assertEquals(updatedReport.getTotalRevenue(), testReportDTO.getTotalRevenue());
    }

    /**
     * save the valid report test
     */
    @Test
    public void validReport_whenSaved_thenCanBeFoundById() {
        try {
            testReport = reportService.save(testReportDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        Report savedReport = reportRepository.findById(testReport.getId()).orElse(null);

        assertNotNull(savedReport);
        assertEquals(testReport, savedReport);
    }

    /**
     * save the invalid report with null company test
     */
    @Test
    public void invalidReportWithNullCompany_whenTryToSave_thenAssertRuntimeException() {
        testReportDTO.setCompanyId(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.save(testReportDTO);
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Company id is required");
    }

    /**
     * save the invalid report with non-existing company test
     */
    @Test
    public void invalidReportWithNonExistingCompany_whenTryToSave_thenAssertRuntimeException() {
        testReportDTO.setCompanyId(UUID.randomUUID());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.save(testReportDTO);
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Company with this id not found");
    }

    /**
     * update the valid report test
     */
    @Test
    public void validReport_whenUpdated_thenCanBeFoundById() {
        try {
            testReport = reportService.save(testReportDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        testReportDTO.setId(testReport.getId());
        testReportDTO.setTotalRevenue(BigDecimal.TEN);

        Report updatedReport;
        try {
            updatedReport = reportService.update(testReportDTO);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        assertEquals(updatedReport.getTotalRevenue(), testReportDTO.getTotalRevenue());
    }

    /**
     * update the invalid report with null company test
     */
    @Test
    public void invalidReportWithNullCompany_whenTryToUpdate_thenAssertRuntimeException() {
        try {
            testReport = reportService.save(testReportDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        testReportDTO.setCompanyId(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.update(testReportDTO);
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Company id is required");
    }

    /**
     * update the invalid report with non-existing company test
     */
    @Test
    public void invalidReportWithNonExistingCompany_whenTryToUpdate_thenAssertRuntimeException() {
        try {
            testReport = reportService.save(testReportDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        testReportDTO.setCompanyId(UUID.randomUUID());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.update(testReportDTO);
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Company with this id not found");
    }

    /**
     * delete the existing report test
     */
    @Test
    public void existingReport_whenDeleted_thenCannotBeFoundById() {
        try {
            testReport = reportService.save(testReportDTO);
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }

        try {
            reportService.delete(testReport.getId());
        } catch (Exception e) {
            throw new RuntimeException();
        }

        assertFalse(reportRepository.existsById(testReport.getId()));
    }

    /**
     * delete the non-existent report test
     */
    @Test
    public void nonexistentReport_whenTryToDelete_thenAssertRuntimeException() {
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.delete(UUID.randomUUID());
        });

        String actualMessage = exception.getMessage();

        assertEquals(actualMessage, "Report with this id not found");
    }
}
