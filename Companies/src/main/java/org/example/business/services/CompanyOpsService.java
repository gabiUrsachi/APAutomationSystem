package org.example.business.services;

import org.example.business.models.CompanyDTO;
import org.example.persistence.collections.Company;
import org.example.persistence.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CompanyOpsService {
    @Autowired
    private CompanyRepository companyRepository;

    public CompanyDTO getCompanyByName(String name){
        Company company = companyRepository.findByName(name);

        return mapToDTO(company);
    }

    public CompanyDTO getCompanyById(UUID uuid){
        Optional<Company> company = companyRepository.findById(uuid);

        return mapToDTO(company.get());
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

    public List<CompanyDTO> mapToDTO(List<Company> companies){

        return companies.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    public CompanyDTO createCompany(CompanyDTO companyDTO) {

        Company company = mapToEntity(companyDTO);
        return mapToDTO(companyRepository.insert(company));
    }

    public List<CompanyDTO> getCompanies() {
        return mapToDTO(companyRepository.findAll());
    }

    public void deleteCompany(UUID identifier) {
        companyRepository.deleteById(identifier);
    }
}
