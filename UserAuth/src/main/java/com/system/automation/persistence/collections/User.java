package com.system.automation.persistence.collections;

import com.system.automation.persistence.utils.Roles;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Document
public class User {
    @Id
    private UUID identifier;

    private String name;

    private UUID companyIdentifier;

    private Set<Roles> roles;
}
