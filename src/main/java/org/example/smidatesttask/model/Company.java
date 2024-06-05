package org.example.smidatesttask.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import java.util.UUID;
import java.sql.Timestamp;

/**
 * Entity for company
 */
@Data
@Entity(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @NotBlank(message = "Company name is required")
    private String name;
    @NotBlank(message = "Company registration number is required")
    private String registrationNumber;
    @NotBlank(message = "Company address is required")
    private String address;
    private Timestamp createdAt;
}
