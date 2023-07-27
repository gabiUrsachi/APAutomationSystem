package org.example.controllers;

import org.example.business.models.CompanyDTO;
import org.example.business.services.CompanyOpsService;
import org.example.persistence.collections.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/companies")
public class CompanyController {
    @Autowired
    private CompanyOpsService companyOpsService;


    @PostMapping
    public CompanyDTO createCompany(@RequestBody CompanyDTO companyDTO) {
        return companyOpsService.createCompany(companyDTO);

    }

    @GetMapping
    public List<CompanyDTO> getCompanies() {

        return companyOpsService.getCompanies();
    }


    @GetMapping("/{identifier}")
    public CompanyDTO getById(@PathVariable UUID identifier) {
        return companyOpsService.getCompanyById(identifier);

    }

    @DeleteMapping("/{identifier}")
    public void deleteById(@PathVariable UUID identifier) {

        companyOpsService.deleteCompany(identifier);

    }
}