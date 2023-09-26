package org.example.persistence.collections;

import org.example.utils.data.Roles;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;
import java.util.UUID;

/**
 * The structure of a document representing app users
 */
@Builder
@Getter
@Setter
@Document
public class User {
    @Id
    @Generated
    private UUID identifier;

    private String username;

    private String password;

    private UUID companyIdentifier;

    private Set<Roles> roles;
}
