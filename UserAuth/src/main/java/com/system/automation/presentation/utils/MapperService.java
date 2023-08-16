package com.system.automation.presentation.utils;

import com.system.automation.persistence.collections.User;
import com.system.automation.presentation.view.RegisterRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class MapperService {
    public User mapToEntity(RegisterRequestDTO registerRequestDTO){
        return User.builder()
                .username(registerRequestDTO.getUsername())
                .password(Password.encrypt(registerRequestDTO.getPassword()))
                .companyIdentifier(registerRequestDTO.getCompanyIdentifier())
                .roles(registerRequestDTO.getRoles())
                .build();
    }
}
