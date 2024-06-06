package org.example.smidatesttask.service;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.mapper.ReportDetailsMapper;
import org.example.smidatesttask.repository.ReportDetailsRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.springframework.stereotype.Service;

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
}
