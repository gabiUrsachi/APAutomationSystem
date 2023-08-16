package com.system.automation.presentation.view;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class LoginResponseDTO {
    String jwsToken;
}
