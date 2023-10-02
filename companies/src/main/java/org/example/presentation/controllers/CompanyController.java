package org.example.presentation.controllers;

import org.example.S3BucketOps;
import org.example.presentation.view.CompanyDTO;
import org.example.presentation.utils.CompanyMapperService;
import org.example.business.services.CompanyService;
import org.example.persistence.collections.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @Autowired
    private CompanyMapperService companyMapperService;

    @PostMapping
    public CompanyDTO createCompany(@RequestBody CompanyDTO companyDTO) {
        CompanyDTO initializedCompany = initializeCompany(companyDTO);
        Company company = companyMapperService.mapToEntity(initializedCompany);

        S3BucketOps.run(company.getCompanyIdentifier().toString());
        return companyMapperService.mapToDTO(companyService.createCompany(company));

    }

    private CompanyDTO initializeCompany(CompanyDTO companyDTO) {
        UUID identifier = UUID.randomUUID();
        companyDTO.setCompanyIdentifier(identifier);

        return companyDTO;
    }

    @GetMapping
    public List<CompanyDTO> getCompanies() {

        return companyMapperService.mapToDTO(companyService.getCompanies());
    }


    @GetMapping("/{identifier}")
    public CompanyDTO getById(@PathVariable UUID identifier) {
        return companyMapperService.mapToDTO(companyService.getCompanyById(identifier));

    }

    @DeleteMapping("/{identifier}")
    public void deleteById(@PathVariable UUID identifier) {

        companyService.deleteCompany(identifier);

    }
}