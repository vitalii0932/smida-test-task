package org.example.smidatesttask.dto;

import lombok.Data;

import java.util.UUID;

/**
 * class company to transfer report details data
 */
@Data
public class ReportDetailsDTO {
    private UUID reportId;
    private String financialData;
    private String comments;
}
