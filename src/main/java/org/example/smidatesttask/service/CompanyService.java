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
        return companyRepository.save(companyMapper.toCompany(companyDTO));
    }
}
