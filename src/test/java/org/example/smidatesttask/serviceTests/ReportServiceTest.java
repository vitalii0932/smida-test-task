package org.example.smidatesttask.serviceTests;

import org.example.smidatesttask.dto.ReportDTO;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.example.smidatesttask.service.ReportService;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

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
        if (reportRepository.findById(testReport.getId()).isPresent()) {
            reportRepository.delete(testReport);
        }
        if (companyRepository.findById(testCompany.getId()).isPresent()) {
            companyRepository.delete(testCompany);
        }
    }
}
