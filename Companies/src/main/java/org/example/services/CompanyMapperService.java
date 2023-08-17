package org.example.services;

import org.example.business.models.CompanyDTO;
import org.example.persistence.collections.Company;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyMapperService {
    public Company mapToEntity(CompanyDTO companyDTO) {
        return Company.builder()
                .companyIdentifier(companyDTO.getCompanyIdentifier())
                .name(companyDTO.getName())
                .build();
    }

    public CompanyDTO mapToDTO(Company company) {
        return CompanyDTO.builder()
                .companyIdentifier(company.getCompanyIdentifier())
                .name(company.getName())
                .build();
    }

    public List<CompanyDTO> mapToDTO(List<Company> companies) {

        return companies.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}


