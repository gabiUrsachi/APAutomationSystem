package com.system.automation.presentation.view;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginRequestDTO {
    private String username;

    private String password;
}
