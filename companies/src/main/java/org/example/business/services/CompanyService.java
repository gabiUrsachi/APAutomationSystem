package org.example.business.services;

import org.example.customexceptions.CompanyNotFoundException;
import org.example.persistence.collections.Company;
import org.example.persistence.repository.CompanyRepository;
import org.example.utils.ErrorMessages;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * This service is used for performing CRUD operations on company related resources
 */
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

        if (company.isEmpty()) {
            throw new CompanyNotFoundException(ErrorMessages.COMPANY_NOT_FOUND, uuid);
        }

        return company.get();
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
