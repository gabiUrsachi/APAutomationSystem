package org.example.presentation.utils;

import org.example.business.services.CompanyService;
import org.example.business.utils.Password;
import org.example.persistence.collections.Company;
import org.example.persistence.collections.User;
import org.example.presentation.view.RegisterRequestDTO;
import org.example.presentation.view.UserDTO;
import org.example.utils.data.Roles;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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


    public UserDTO mapToDTO(User user) {
        Company company = companyService.getCompanyById(user.getCompanyIdentifier());

        return UserDTO.builder()
                .identifier(user.getIdentifier())
                .username(user.getUsername())
                .company(company.getName())
                .roles(user.getRoles())
                .build();
    }

    public List<UserDTO> mapToDTO(List<User> users) {
        return users.stream()
                .filter(user -> !user.getRoles().contains(Roles.ADMIN))
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}
