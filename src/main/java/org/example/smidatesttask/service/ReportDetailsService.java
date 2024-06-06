package org.example.smidatesttask.service;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.mapper.ReportDetailsMapper;
import org.example.smidatesttask.model.ReportDetails;
import org.example.smidatesttask.repository.ReportDetailsRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * service for working with the logic of the report details model
 */
@Service
@RequiredArgsConstructor
public class ReportDetailsService {

    private final JsonValidationService jsonValidationService;
    private final ReportDetailsRepository reportDetailsRepository;
    private final ReportRepository reportRepository;
    private final ReportDetailsMapper reportDetailsMapper;

    /**
     * get report details function
     *
     * @param reportId - report id
     * @return the report details
     * @throws RuntimeException if report was not found
     */
    @Transactional(readOnly = true)
    public ReportDetails getReportDetails(UUID reportId) throws RuntimeException {
        return reportDetailsRepository.findById(reportId).orElseThrow(
                () -> new RuntimeException("Report details with this id not found")
        );
    }
}
