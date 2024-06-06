package org.example.smidatesttask.mapper;

import org.example.smidatesttask.dto.ReportDTO;
import org.example.smidatesttask.model.Report;
import org.mapstruct.Mapper;

/**
 * mapper interface for converting between Report and ReportDTO objects
 */
@Mapper(componentModel = "spring")
public interface ReportMapper {
    Report toReport(ReportDTO reportDTO);
    ReportDTO toReportDTO(Report report);
}
