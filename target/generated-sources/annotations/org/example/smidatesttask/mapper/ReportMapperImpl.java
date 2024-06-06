package org.example.smidatesttask.mapper;

import javax.annotation.processing.Generated;
import org.example.smidatesttask.dto.ReportDTO;
import org.example.smidatesttask.model.Report;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-07T01:06:08+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.23 (Oracle Corporation)"
)
@Component
public class ReportMapperImpl implements ReportMapper {

    @Override
    public Report toReport(ReportDTO reportDTO) {
        if ( reportDTO == null ) {
            return null;
        }

        Report report = new Report();

        report.setId( reportDTO.getId() );
        report.setReportDate( reportDTO.getReportDate() );
        report.setTotalRevenue( reportDTO.getTotalRevenue() );
        report.setNetProfit( reportDTO.getNetProfit() );

        return report;
    }

    @Override
    public ReportDTO toReportDTO(Report report) {
        if ( report == null ) {
            return null;
        }

        ReportDTO reportDTO = new ReportDTO();

        reportDTO.setId( report.getId() );
        reportDTO.setReportDate( report.getReportDate() );
        reportDTO.setTotalRevenue( report.getTotalRevenue() );
        reportDTO.setNetProfit( report.getNetProfit() );

        return reportDTO;
    }
}
