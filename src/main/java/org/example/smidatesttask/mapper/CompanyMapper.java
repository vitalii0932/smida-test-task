package org.example.smidatesttask.mapper;

import org.example.smidatesttask.dto.CompanyDTO;
import org.example.smidatesttask.model.Company;
import org.mapstruct.Mapper;

/**
 * mapper interface for converting between Company and CompanyDTO objects
 */
@Mapper(componentModel = "spring")
public interface CompanyMapper {
    Company toCompany(CompanyDTO companyDTO);
    CompanyDTO toCompanyDTO(Company company);
}
