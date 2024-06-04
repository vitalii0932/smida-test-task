package org.example.smidatesttask.repository;

import org.example.smidatesttask.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository for Company entities
 */
public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
