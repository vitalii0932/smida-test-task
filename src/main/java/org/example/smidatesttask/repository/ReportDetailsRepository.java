package org.example.smidatesttask.repository;

import org.example.smidatesttask.models.ReportDetails;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * repository for ReportDetails entities
 */
@Repository
public interface ReportDetailsRepository extends MongoRepository<ReportDetails, UUID> {
}
