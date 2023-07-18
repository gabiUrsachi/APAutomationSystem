package com.system.automation.ap.purchaseOrder.persistence.collections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class User {
    @Id
    private UUID identifier;

    private String name;

    private UUID companyIdentifier;
}
