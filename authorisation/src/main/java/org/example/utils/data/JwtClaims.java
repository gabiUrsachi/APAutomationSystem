package org.example.utils.data;

import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class JwtClaims {
    private String username;
    private UUID companyUUID;
    Set<Roles> roles;
}
