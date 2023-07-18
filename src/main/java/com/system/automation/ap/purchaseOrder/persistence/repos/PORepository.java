package com.system.automation.ap.purchaseOrder.persistence.repos;

import com.system.automation.ap.purchaseOrder.persistence.collections.Document;
import com.system.automation.ap.purchaseOrder.utils.DocumentType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PORepository extends MongoRepository<Document, String> {
    Optional<Document> findByIdentifierAndDocumentType(String Identifier, DocumentType documentType);
}
