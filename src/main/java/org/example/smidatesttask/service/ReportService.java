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

import java.lang.reflect.Field;
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
     * find report by its id
     *
     * @param id - report id
     * @return a report
     * @throws RuntimeException if report not found
     */
    private Report findReportById(UUID id) throws RuntimeException {
        return reportRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Report with this id not found")
        );
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

    /**
     * update report in db function
     *
     * @param reportDTO - report data from user
     * @return the updated report
     * @throws RuntimeException if report not found
     * @throws IllegalAccessException if class arguments not correct
     * @throws ValidationException if report not correct
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public Report update(ReportDTO reportDTO) throws RuntimeException, IllegalAccessException, ValidationException {
        Report reportNewData = reportMapper.toReport(reportDTO);
        Company reportCompany = companyService.findCompanyById(reportDTO.getCompanyId());
        reportNewData.setCompany(reportCompany);

        validationService.isValid(reportNewData);

        Report reportToUpdate = findReportById(reportNewData.getId());

        Field[] fields = reportNewData.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(reportNewData);
            if (value != null && !field.getName().equals("createdAt")) {
                Field reportField;
                try {
                    reportField = reportToUpdate.getClass().getDeclaredField(field.getName());
                    reportField.setAccessible(true);
                    reportField.set(reportToUpdate, value);
                } catch (NoSuchFieldException e) {
                    // ignore fields that are not found in the reportToUpdate object
                }
            }
        }

        return reportRepository.save(reportToUpdate);
    }

    /**
     * delete entity from db function
     *
     * @param id - entity id
     * @throws RuntimeException if report not found
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public void delete(UUID id) throws RuntimeException {
        findReportById(id);
        reportRepository.deleteById(id);
    }
}
