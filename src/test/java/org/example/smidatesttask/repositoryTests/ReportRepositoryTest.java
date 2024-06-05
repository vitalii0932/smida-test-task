package org.example.smidatesttask.repositoryTests;

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
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReportRepository tests
 */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
public class ReportRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ReportRepository reportRepository;

    private Company testCompany;

    private Report testReport;

    /**
     * set up the test report
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
    }

    /**
     * tear down the test report
     */
    @After
    public void tearDown() {
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
    public void report_whenSaved_thenCanBeFoundById() {
        Report savedReport = reportRepository.findById(testReport.getId()).orElse(null);

        assertNotNull(savedReport);
        assertEquals(testReport, savedReport);
    }

    /**
     * update test
     */
    @Test
    public void report_whenUpdated_thenCanBeFoundById() {
        testReport.setTotalRevenue(BigDecimal.TEN);
        reportRepository.save(testReport);

        Optional<Report> updatedReport = reportRepository.findById(testReport.getId());

        assertTrue(updatedReport.isPresent());
        assertEquals(BigDecimal.TEN, updatedReport.get().getTotalRevenue());
    }

    /**
     * delete test
     */
    @Test
    public void report_whenDeleted_thenCannotBeFoundById() {
        reportRepository.delete(testReport);

        assertFalse(reportRepository.findById(testReport.getId()).isPresent());
    }

    /**
     * getAllByCompany function test
     */
    @Test
    public void report_getAllByCompany_thenOneReportInResultList() {
        List<Report> reports = reportRepository.getAllByCompany(testCompany);

        assertNotNull(reports);
        assertEquals(1, reports.size());
        assertEquals(testReport.getId(), reports.get(0).getId());
    }
}
