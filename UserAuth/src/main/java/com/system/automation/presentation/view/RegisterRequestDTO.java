package com.system.automation.presentation.view;


import com.system.automation.persistence.utils.Roles;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class RegisterRequestDTO {
    private String username;

    private String password;

    private UUID companyIdentifier;

    private Set<Roles> roles;
}
