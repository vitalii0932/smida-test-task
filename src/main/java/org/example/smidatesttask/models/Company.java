package org.example.smidatesttask.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    private String name;
    private String registrationNumber;
    private String address;
    private Timestamp createdAt;
}
