package org.example.smidatesttask.service;

import lombok.RequiredArgsConstructor;
import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.mapper.CompanyMapper;
import org.example.smidatesttask.models.Company;
import org.example.smidatesttask.repository.CompanyRepository;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * service for working with the logic of the company model
 */
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    /**
     * save company in db function
     *
     * @param companyDTO - company data from user
     * @return the saved company
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public Company save(CompanyDTO companyDTO) {
        var companyToSave = companyMapper.toCompany(companyDTO);
        companyToSave.setCreatedAt(Timestamp.from(Instant.now()));
        return companyRepository.save(companyMapper.toCompany(companyDTO));
    }

    /**
     * update company in db function
     *
     * @param companyDTO - company data from user
     * @return the saved company
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Retryable(maxAttempts = 5)
    public Company update(CompanyDTO companyDTO) throws RuntimeException, IllegalAccessException {
        Company companyToUpdate = companyRepository.findById(companyDTO.getId()).orElseThrow(
                () -> new RuntimeException("Company with this id now found")
        );

        Field[] fields = companyDTO.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(companyDTO);
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
}
