package org.example.business.services;

import org.example.business.models.CompanyDTO;
import org.example.persistence.collections.Company;
import org.example.persistence.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyOpsService {
    @Autowired
    private CompanyRepository companyRepository;

    public CompanyDTO getCompanyByName(String name){
        Company company = companyRepository.findByName(name);

        return mapToDTO(company);
    }

    public Company mapToEntity(CompanyDTO companyDTO){
        return Company.builder()
                .companyIdentifier(companyDTO.getCompanyIdentifier())
                .name(companyDTO.getName())
                .build();
    }

    public CompanyDTO mapToDTO(Company company){
        return CompanyDTO.builder()
                .companyIdentifier(company.getCompanyIdentifier())
                .name(company.getName())
                .build();
    }
}
