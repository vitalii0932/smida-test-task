package org.example.smidatesttask.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * class report to transfer report data
 */
@Data
public class ReportDTO {
    private UUID id;
    private UUID companyId;
    private Timestamp reportDate;
    private BigDecimal totalRevenue;
    private BigDecimal netProfit;
}
