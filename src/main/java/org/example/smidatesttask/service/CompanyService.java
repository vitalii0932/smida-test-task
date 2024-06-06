package org.example.smidatesttask.service;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.exception.ValidationException;
import org.example.smidatesttask.mapper.CompanyMapper;
import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.repository.CompanyRepository;
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
        return companyRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Company with this id not found")
        );
    }

    /**
     * save company in db function
     *
     * @param companyDTO - company data from user
     * @return the saved company
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
    public void delete(UUID id) throws RuntimeException {
        findCompanyById(id);
        companyRepository.deleteById(id);
    }
}
