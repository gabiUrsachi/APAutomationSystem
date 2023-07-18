package com.system.automation.ap.purchaseOrder.services;

import com.system.automation.ap.purchaseOrder.persistence.collections.Document;
import com.system.automation.ap.purchaseOrder.persistence.repos.PORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class POService {
    @Autowired
    private PORepository poRepository;

//    public Document getByIdentifier(String identifier){
//
//    }

}
