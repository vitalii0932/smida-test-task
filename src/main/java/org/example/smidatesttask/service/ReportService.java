package org.example.smidatesttask.service;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.mapper.ReportMapper;
import org.example.smidatesttask.repository.ReportRepository;
import org.springframework.stereotype.Service;

/**
 * service for working with the logic of the report model
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final ValidationService validationService;
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;
}
