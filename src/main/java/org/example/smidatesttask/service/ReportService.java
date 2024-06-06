package org.example.smidatesttask.service;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.mapper.ReportMapper;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.springframework.stereotype.Service;
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
        Company selectedCompany = companyRepository.findById(companyId).orElseThrow(
                () -> new RuntimeException("The company with this id doesn't exist in the db")
        );

        return reportRepository.getAllByCompany(selectedCompany);
    }
}
