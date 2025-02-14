package org.example.smidatesttask.repository;

import org.example.smidatesttask.model.Company;
import org.example.smidatesttask.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * repository for Report entities
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, UUID> {
    /**
     * get all reports by some company
     *
     * @param company - selected company
     * @return a list of reports
     */
    List<Report> getAllByCompany(Company company);
}
