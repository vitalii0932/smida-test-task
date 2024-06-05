package org.example.smidatesttask.repository;

import org.example.smidatesttask.models.Company;
import org.example.smidatesttask.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * repository for Report entities
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {

}
