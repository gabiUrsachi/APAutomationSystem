package org.example.business.services;

import org.example.persistence.collections.Company;
import org.example.persistence.repository.CompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

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
