package org.example.presentation.controllers;

import org.example.S3BucketOps;
import org.example.business.services.CompanyService;
import org.example.persistence.collections.Company;
import org.example.presentation.utils.CompanyMapperService;
import org.example.presentation.view.CompanyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    private static final Logger logger = LoggerFactory.getLogger(CompanyController.class);

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyMapperService companyMapperService;

    @PostMapping
    public CompanyDTO createCompany(@RequestBody CompanyDTO companyDTO) {
        logger.info("[POST request] -> create company:{}", companyDTO.getName());

        Company company = companyMapperService.mapToEntity(companyDTO);
        Company savedCompany = companyService.createCompany(company);

        HeadBucketResponse headBucketResponse = S3BucketOps.createS3Bucket(savedCompany.getCompanyIdentifier().toString());

        if(headBucketResponse != null){
            logger.warn("Successfully created S3 bucket for company {}", companyDTO.getName());
            companyService.updateCompany(savedCompany);
        }

        return companyMapperService.mapToDTO(savedCompany);
    }

    @GetMapping
    public List<CompanyDTO> getCompanies() {
        logger.info("[GET request] -> get all companies");
        return companyMapperService.mapToDTO(companyService.getCompanies());
    }


    @GetMapping("/{identifier}")
    public CompanyDTO getById(@PathVariable UUID identifier) {
        logger.info("[GET request] -> get company by UUID: {}", identifier);
        return companyMapperService.mapToDTO(companyService.getCompanyById(identifier));
    }

    @DeleteMapping("/{identifier}")
    public void deleteById(@PathVariable UUID identifier) {
        logger.info("[DELETE request] -> remove company by UUID: {}", identifier);
        companyService.deleteCompany(identifier);
    }
}