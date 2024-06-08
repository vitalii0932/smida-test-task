package org.example.smidatesttask.mapper;

import javax.annotation.processing.Generated;
import org.example.smidatesttask.dto.ReportDetailsDTO;
import org.example.smidatesttask.model.ReportDetails;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-08T23:27:53+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.23 (Oracle Corporation)"
)
@Component
public class ReportDetailsMapperImpl implements ReportDetailsMapper {

    @Override
    public ReportDetails toReportDetails(ReportDetailsDTO reportDetailsDTO) {
        if ( reportDetailsDTO == null ) {
            return null;
        }

        ReportDetails reportDetails = new ReportDetails();

        reportDetails.setReportId( reportDetailsDTO.getReportId() );
        reportDetails.setFinancialData( reportDetailsDTO.getFinancialData() );
        reportDetails.setComments( reportDetailsDTO.getComments() );

        return reportDetails;
    }

    @Override
    public ReportDetailsDTO toReportDetailsDTO(ReportDetails reportDetails) {
        if ( reportDetails == null ) {
            return null;
        }

        ReportDetailsDTO reportDetailsDTO = new ReportDetailsDTO();

        reportDetailsDTO.setReportId( reportDetails.getReportId() );
        reportDetailsDTO.setFinancialData( reportDetails.getFinancialData() );
        reportDetailsDTO.setComments( reportDetails.getComments() );

        return reportDetailsDTO;
    }
}
