package org.example.presentation.utils;

import org.example.business.services.CompanyService;
import org.example.business.utils.Password;
import org.example.persistence.collections.User;
import org.example.presentation.view.RegisterRequestDTO;
import org.springframework.stereotype.Service;

/**
 * This service is used for entity <-> dto conversions
 */
@Service
public class UserMapperService {
    private final CompanyService companyService;

    public UserMapperService(CompanyService companyService) {
        this.companyService = companyService;
    }

    /**
     * It creates an entity with the same properties as the received dto
     *
     * @param registerRequestDTO DTO to be converted
     * @return created entity
     */
    public User mapToEntity(RegisterRequestDTO registerRequestDTO) {
        // if company doesn't exist, an exception will be thrown
        companyService.getCompanyById(registerRequestDTO.getCompanyIdentifier());

        return User.builder()
                .username(registerRequestDTO.getUsername())
                .password(Password.encrypt(registerRequestDTO.getPassword()))
                .companyIdentifier(registerRequestDTO.getCompanyIdentifier())
                .roles(registerRequestDTO.getRoles())
                .build();
    }
}
