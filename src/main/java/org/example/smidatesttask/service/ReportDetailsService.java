package org.example.smidatesttask.service;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.ReportDetailsDTO;
import org.example.smidatesttask.mapper.ReportDetailsMapper;
import org.example.smidatesttask.model.ReportDetails;
import org.example.smidatesttask.repository.ReportDetailsRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * service for working with the logic of the report details model
 */
@Service
@RequiredArgsConstructor
public class ReportDetailsService {

    private final JsonService jsonService;
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

    /**
     * save report details in db
     *
     * @param reportDetailsDTO - report details data from user
     * @return a saved report details
     * @throws RuntimeException is something was wrong
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public ReportDetails save(ReportDetailsDTO reportDetailsDTO) throws RuntimeException {
        if (!reportRepository.existsById(reportDetailsDTO.getReportId())) {
            throw new RuntimeException("Report with this id was not found");
        }
        if (reportDetailsRepository.existsById(reportDetailsDTO.getReportId())) {
            throw new RuntimeException("Report with this id is already taken");
        }

        reportDetailsDTO.setFinancialData(jsonService.strToJsonNode(reportDetailsDTO.getFinancialData()));

        ReportDetails reportDetailsToSave = reportDetailsMapper.toReportDetails(reportDetailsDTO);

        return reportDetailsRepository.save(reportDetailsToSave);
    }

    /**
     * update report details in db
     *
     * @param reportDetailsDTO - report details data from user
     * @return a saved report details
     * @throws RuntimeException is something was wrong
     * @throws IllegalAccessException if class arguments not correct
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public ReportDetails update(ReportDetailsDTO reportDetailsDTO) throws RuntimeException, IllegalAccessException {
        reportDetailsDTO.setFinancialData(jsonService.strToJsonNode(reportDetailsDTO.getFinancialData()));
        ReportDetails reportDetailsNewData = reportDetailsMapper.toReportDetails(reportDetailsDTO);
        
        ReportDetails reportDetailsToUpdate = getReportDetails(reportDetailsDTO.getReportId());

        Field[] fields = reportDetailsNewData.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(reportDetailsNewData);
            if (value != null && !field.getName().equals("createdAt")) {
                Field reportField;
                try {
                    reportField = reportDetailsToUpdate.getClass().getDeclaredField(field.getName());
                    reportField.setAccessible(true);
                    reportField.set(reportDetailsToUpdate, value);
                } catch (NoSuchFieldException e) {
                    // ignore fields that are not found in the reportDetailsToUpdate object
                }
            }
        }

        return reportDetailsRepository.save(reportDetailsToUpdate);
    }

    /**
     * delete entity from db function
     *
     * @param id - entity id
     * @throws RuntimeException if report details not found
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public void delete(UUID id) throws RuntimeException {
        reportDetailsRepository.delete(getReportDetails(id));
    }
}
