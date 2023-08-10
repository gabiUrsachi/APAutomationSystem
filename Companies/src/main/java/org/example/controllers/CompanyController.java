package org.example.controllers;

import org.example.business.models.CompanyDTO;
import org.example.business.services.CompanyMapperService;
import org.example.business.services.CompanyOpsService;
import org.example.persistence.collections.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/companies")
@CrossOrigin(origins = "http://localhost:4200/")
public class CompanyController {
    @Autowired
    private CompanyOpsService companyOpsService;

    @Autowired
    private CompanyMapperService companyMapperService;

    @PostMapping
    public CompanyDTO createCompany(@RequestBody CompanyDTO companyDTO) {
        CompanyDTO initializedCompany = initializeCompany(companyDTO);
        Company company = companyMapperService.mapToEntity(initializedCompany);
        return companyMapperService.mapToDTO(companyOpsService.createCompany(company));

    }

    private CompanyDTO initializeCompany(CompanyDTO companyDTO) {
        UUID identifier = UUID.randomUUID();
        companyDTO.setCompanyIdentifier(identifier);

        return companyDTO;
    }

    @GetMapping
    public List<CompanyDTO> getCompanies() {

        return companyMapperService.mapToDTO(companyOpsService.getCompanies());
    }


    @GetMapping("/{identifier}")
    public CompanyDTO getById(@PathVariable UUID identifier) {
        return companyMapperService.mapToDTO(companyOpsService.getCompanyById(identifier));

    }

    @DeleteMapping("/{identifier}")
    public void deleteById(@PathVariable UUID identifier) {

        companyOpsService.deleteCompany(identifier);

    }
}