package org.example.smidatesttask.models;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Entity for report
 */
@Data
@Entity(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @ManyToOne
    @NotNull(message = "Company is required")
    @JoinColumn(name = "company_id")
    private Company company;
    private Timestamp reportDate;
    private BigDecimal totalRevenue;
    private BigDecimal netProfit;
}
