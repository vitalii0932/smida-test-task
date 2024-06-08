package org.example.smidatesttask.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportDTO reportDTO = (ReportDTO) o;
        return Objects.equals(id, reportDTO.id) && Objects.equals(companyId, reportDTO.companyId) && Objects.equals(totalRevenue, reportDTO.totalRevenue) && Objects.equals(netProfit, reportDTO.netProfit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, companyId, reportDate, totalRevenue, netProfit);
    }
}
