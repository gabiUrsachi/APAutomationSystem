package org.example.presentation.utils;

import org.example.business.utils.Password;
import org.example.persistence.collections.User;
import org.example.presentation.view.RegisterRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class UserMapperService {
    public User mapToEntity(RegisterRequestDTO registerRequestDTO){
        return User.builder()
                .username(registerRequestDTO.getUsername())
                .password(Password.encrypt(registerRequestDTO.getPassword()))
                .companyIdentifier(registerRequestDTO.getCompanyIdentifier())
                .roles(registerRequestDTO.getRoles())
                .build();
    }
}
