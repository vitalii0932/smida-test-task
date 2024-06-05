package org.example.smidatesttask.mapper;

import javax.annotation.processing.Generated;
import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.model.Company;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-05T18:32:07+0300",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 11.0.23 (Oracle Corporation)"
)
@Component
public class CompanyMapperImpl implements CompanyMapper {

    @Override
    public Company toCompany(CompanyDTO companyDTO) {
        if ( companyDTO == null ) {
            return null;
        }

        Company company = new Company();

        company.setId( companyDTO.getId() );
        company.setName( companyDTO.getName() );
        company.setRegistrationNumber( companyDTO.getRegistrationNumber() );
        company.setAddress( companyDTO.getAddress() );
        company.setCreatedAt( companyDTO.getCreatedAt() );

        return company;
    }

    @Override
    public CompanyDTO toCompanyDTO(Company company) {
        if ( company == null ) {
            return null;
        }

        CompanyDTO companyDTO = new CompanyDTO();

        companyDTO.setId( company.getId() );
        companyDTO.setName( company.getName() );
        companyDTO.setRegistrationNumber( company.getRegistrationNumber() );
        companyDTO.setAddress( company.getAddress() );
        companyDTO.setCreatedAt( company.getCreatedAt() );

        return companyDTO;
    }
}
