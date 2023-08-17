package org.example.services;

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

    public Company getCompanyByName(String name) {

        return companyRepository.findByName(name);
    }

    public Company getCompanyById(UUID uuid) {
        Optional<Company> company = companyRepository.findById(uuid);

        // De pus exceptie pe viitor
        return company.orElse(null);
    }


    public Company createCompany(Company company) {

        return companyRepository.insert(company);
    }

    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    public void deleteCompany(UUID identifier) {
        companyRepository.deleteById(identifier);
    }
}
