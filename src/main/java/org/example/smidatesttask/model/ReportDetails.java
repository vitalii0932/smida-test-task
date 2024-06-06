package org.example.smidatesttask.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

/**
 * Document for Report Details
 */
@Data
@Document(collection = "reportDetails")
public class ReportDetails {
    @Id
    private UUID reportId;
    @Field("financial_data")
    private String financialData;
    private String comments;
}
