package org.example.smidatesttask.mapper;

import org.example.smidatesttask.dto.ReportDetailsDTO;
import org.example.smidatesttask.model.ReportDetails;
import org.mapstruct.Mapper;

/**
 * mapper interface for converting between ReportDetails and ReportDetailsDTO objects
 */
@Mapper(componentModel = "spring")
public interface ReportDetailsMapper {
    ReportDetails toReportDetails(ReportDetailsDTO reportDetailsDTO);
    ReportDetailsDTO toReportDetailsDTO(ReportDetails reportDetails);
}
