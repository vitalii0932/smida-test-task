package org.example.smidatesttask.dto;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

/**
 * class company to transfer company data
 */
@Data
public class CompanyDTO {
    private UUID id;
    private String name;
    private String registrationNumber;
    private String address;
    private Timestamp createdAt;
}
