package org.example.business.services;

import org.example.S3BucketOps;
import org.example.persistence.collections.Company;
import org.example.persistence.repository.CompanyRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.Bucket;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchedulerService {
    private final CompanyRepository companyRepository;

    public SchedulerService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Scheduled(cron = "1 * * * * *")
    public void checkBucketsExistence() {
        List<Company> existingCompanies = this.companyRepository.findAll();
        List<Bucket> existingS3Buckets = S3BucketOps.getS3Buckets();

        List<String> bucketsName = existingS3Buckets.stream().map((Bucket::name)).collect(Collectors.toList());
        List<String> nonExistentBuckets = existingCompanies.stream()
                .map((company -> company.getCompanyIdentifier().toString()))
                .filter(company -> !bucketsName.contains(company))
                .collect(Collectors.toList());

        for (String nonExistingBucket : nonExistentBuckets) {
            S3BucketOps.createS3Bucket(nonExistingBucket);
        }
    }
}
