package org.system.business.services;

import org.example.persistence.collections.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.system.persistence.repository.PurchaseOrderRepository;

import java.util.Set;

@Service
public class PurchaseOrderService {
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

//    @Autowired
//    private CompanyRepository companyRepository;

    public Set<Company> getCompaniesByName(String name){
        return null;
    }
}
