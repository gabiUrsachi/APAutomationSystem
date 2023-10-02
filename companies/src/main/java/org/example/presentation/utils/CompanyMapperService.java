package org.example.presentation.utils;

import org.example.presentation.view.CompanyDTO;
import org.example.persistence.collections.Company;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This service is used for entity <-> dto conversions
 */
@Service
public class CompanyMapperService {

    /**
     * It creates an entity with the same properties as the received dto
     *
     * @param companyDTO DTO to be converted
     * @return created entity
     */
    public Company mapToEntity(CompanyDTO companyDTO) {
        return Company.builder()
                //.companyIdentifier(companyDTO.getCompanyIdentifier())
                .name(companyDTO.getName())
                .build();
    }

    /**
     * It creates a Data Transfer Object with the same properties as the received entity
     *
     * @param company entity to be converted
     * @return created DTO
     */
    public CompanyDTO mapToDTO(Company company) {
        return CompanyDTO.builder()
                .companyIdentifier(company.getCompanyIdentifier())
                .name(company.getName())
                .build();
    }

    /**
     * It creates a list of Data Transfer Object with the same properties as the received entities
     *
     * @param companies entities to be converted
     * @return created DTOs
     */
    public List<CompanyDTO> mapToDTO(List<Company> companies) {

        return companies.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}


