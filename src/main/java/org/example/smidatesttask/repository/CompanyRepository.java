package org.example.smidatesttask.repository;

import org.example.smidatesttask.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for Company entities
 */
@Repository
public interface CompanyRepository extends JpaRepository<Company, UUID> {
}
