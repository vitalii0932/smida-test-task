package org.example.smidatesttask.service;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.ReportDTO;
import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.mapper.ReportMapper;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * service for working with the logic of the report model
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ValidationService validationService;
    private final CompanyService companyService;
    private final ReportRepository reportRepository;
    private final CompanyRepository companyRepository;
    private final ReportMapper reportMapper;

    /**
     * get all reports by some company function
     *
     * @param companyId - selected companyId
     * @return a list of reports
     * @throws RuntimeException if company was not found
     */
    @Transactional(readOnly = true)
    public List<Report> getAllReportByCompany(UUID companyId) throws RuntimeException {
        Company selectedCompany = companyService.findCompanyById(companyId);

        return reportRepository.getAllByCompany(selectedCompany);
    }

    /**
     * save report in db function
     *
     * @param reportDTO - report data from user
     * @return a saved report
     * @throws RuntimeException if reports company not found in db
     * @throws ValidationException if report is not valid
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public Report save(ReportDTO reportDTO) throws RuntimeException, ValidationException {
        Report reportToSave = reportMapper.toReport(reportDTO);
        Company reportCompany = companyService.findCompanyById(reportDTO.getCompanyId());
        reportToSave.setCompany(reportCompany);

        validationService.isValid(reportToSave);

        return reportRepository.save(reportToSave);
    }
}
