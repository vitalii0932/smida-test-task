package org.example.smidatesttask.service;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.mapper.CompanyMapper;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.example.smidatesttask.repository.CompanyRepository;
import org.example.smidatesttask.repository.ReportDetailsRepository;
import org.example.smidatesttask.repository.ReportRepository;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * service for working with the logic of the company model
 */
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final ValidationService validationService;
    private final CompanyRepository companyRepository;
    private final ReportRepository reportRepository;
    private final ReportDetailsRepository reportDetailsRepository;
    private final CompanyMapper companyMapper;

    /**
     * get all companies
     *
     * @return a list of companies
     */
    @Transactional(readOnly = true)
    public List<Company> getAll() {
        return companyRepository.findAll();
    }

    /**
     * find company by its id
     *
     * @param id - company id
     * @return a company
     * @throws RuntimeException if company was not found
     */
    @Transactional(readOnly = true)
    public Company findCompanyById(UUID id) throws RuntimeException {
        if (id == null) {
            throw new RuntimeException("Company id is required");
        }
        return companyRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Company with this id not found")
        );
    }

    /**
     * find company dto by its id
     *
     * @param id - company id
     * @return a company
     * @throws RuntimeException if company was not found
     */
    @Transactional(readOnly = true)
    public CompanyDTO findCompanyDTOById(UUID id) throws RuntimeException {
        return companyMapper.toCompanyDTO(findCompanyById(id));
    }

    /**
     * create or update company function
     *
     * @param companyDTO - company data from user
     * @return the saved company
     * @throws Exception if something was wrong
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public Company createOrUpdateCompany(CompanyDTO companyDTO) throws Exception {
        if (companyDTO.getId() == null) {
            return save(companyDTO);
        } else {
            return update(companyDTO);
        }
    }

    /**
     * save company in db function
     *
     * @param companyDTO - company data from user
     * @return the saved company
     * @throws ValidationException if company is not valid
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public Company save(CompanyDTO companyDTO) throws ValidationException {
        var companyToSave = companyMapper.toCompany(companyDTO);
        companyToSave.setCreatedAt(Timestamp.from(Instant.now()));

        validationService.isValid(companyToSave);

        companyRepository.save(companyToSave);

        return companyToSave;
    }

    /**
     * update company in db function
     *
     * @param companyDTO - company data from user
     * @return the updated company
     * @throws RuntimeException if company not found
     * @throws IllegalAccessException if class arguments not correct
     * @throws ValidationException if company not correct
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public Company update(CompanyDTO companyDTO) throws RuntimeException, IllegalAccessException, ValidationException {
        Company companyNewData = companyMapper.toCompany(companyDTO);

        validationService.isValid(companyNewData);

        Company companyToUpdate = findCompanyById(companyDTO.getId());

        Field[] fields = companyNewData.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(companyNewData);
            if (value != null && !field.getName().equals("createdAt")) {
                Field companyField;
                try {
                    companyField = companyToUpdate.getClass().getDeclaredField(field.getName());
                    companyField.setAccessible(true);
                    companyField.set(companyToUpdate, value);
                } catch (NoSuchFieldException e) {
                    // ignore fields that are not found in the companyToUpdate object
                }
            }
        }

        return companyRepository.save(companyToUpdate);
    }

    /**
     * delete entity from db function
     *
     * @param id - entity id
     * @throws RuntimeException if company not found
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public void delete(UUID id) throws Exception {
        Company company = findCompanyById(id);
        List<Report> reportsToDelete = reportRepository.getAllByCompany(company);

        for (var report : reportsToDelete) {
            try {
                reportDetailsRepository.deleteById(report.getId());
                reportRepository.delete(report);
            } catch (Exception e) {
                // skip
            }
        }

        companyRepository.deleteById(id);
    }
}
