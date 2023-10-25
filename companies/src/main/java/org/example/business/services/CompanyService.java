package org.example.business.services;

import org.example.S3BucketOps;
import org.example.customexceptions.ResourceNotFoundException;
import org.example.persistence.collections.Company;
import org.example.persistence.repository.CompanyRepository;
import org.example.utils.ErrorMessages;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public Company updateCompany(Company company){
        company.setHasBucket(true);

        return companyRepository.save(company);
    }

    public List<Company> getCompanies() {
        return companyRepository.findAll();
    }

    public void deleteCompany(UUID identifier) {
        companyRepository.deleteById(identifier);
    }

    @Scheduled(cron = "1 * * * * *")
    public void checkBucketsExistence() {
        Set<Company> existingCompanies = this.companyRepository.findAllByHasBucket(true);

        for(Company company: existingCompanies){
            HeadBucketResponse headBucketResponse = S3BucketOps.createS3Bucket(company.getCompanyIdentifier().toString());

            if(headBucketResponse != null){
                this.updateCompany(company);
            }
        }
    }
}

