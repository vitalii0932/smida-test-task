package org.example.smidatesttask.models;

import com.fasterxml.jackson.databind.util.JSONPObject;
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
    private JSONPObject financialData;
    private String comments;
}
