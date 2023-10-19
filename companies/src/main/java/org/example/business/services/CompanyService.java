package org.example.business.services;

import org.example.customexceptions.ResourceNotFoundException;
import org.example.persistence.collections.Company;
import org.example.persistence.repository.CompanyRepository;
import org.example.presentation.view.CompanyDTO;
import org.example.utils.ErrorMessages;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.example.S3BucketOps;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.*;

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
            throw new ResourceNotFoundException(ErrorMessages.COMPANY_NOT_FOUND, uuid.toString());
        }

        return company.get();
    }


    public Company createCompany(Company company) {
        company.setCompanyIdentifier(UUID.randomUUID());

        return companyRepository.insert(company);
    }

    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    public void deleteCompany(UUID identifier) {
        companyRepository.deleteById(identifier);
    }

    @Scheduled(fixedRate = 500)
    public void createBucketIfNotExists() {
        List<Company> companies = getCompanies();
        List<Bucket> buckets = S3BucketOps.getBucketList();

        for(Company company:companies){
            boolean companyExists = buckets.stream().anyMatch(bucket -> bucket.name().equals(String.valueOf(company.getCompanyIdentifier())));
            if(!companyExists){
                S3BucketOps.createS3Bucket(company.getCompanyIdentifier().toString());
            }
        }


    }
}

