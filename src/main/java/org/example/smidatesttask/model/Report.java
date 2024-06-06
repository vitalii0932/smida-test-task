package org.example.smidatesttask.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id) && Objects.equals(company, report.company) && Objects.equals(totalRevenue, report.totalRevenue) && Objects.equals(netProfit, report.netProfit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, company, reportDate, totalRevenue, netProfit);
    }
}
