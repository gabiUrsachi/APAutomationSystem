package org.example.presentation.view;


import org.example.Roles;
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
