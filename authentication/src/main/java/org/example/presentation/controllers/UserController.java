package org.example.presentation.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.business.services.UserService;
import org.example.persistence.collections.User;
import org.example.presentation.utils.UserMapperService;
import org.example.presentation.view.LoginRequestDTO;
import org.example.presentation.view.LoginResponseDTO;
import org.example.presentation.view.RegisterRequestDTO;
import org.example.services.AuthorisationService;
import org.example.utils.Roles;
import org.example.utils.TokenHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200/")
public class UserController {
    private final UserService userService;
    private final UserMapperService userMapperService;
    private final AuthorisationService authorisationService;

    public UserController(UserService userService, UserMapperService userMapperService, AuthorisationService authorisationService) {
        this.userService = userService;
        this.userMapperService = userMapperService;
        this.authorisationService = authorisationService;
    }

    @Operation(summary = "creates a new user account")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Account successfully created"),
                    @ApiResponse(responseCode = "401", description = "Invalid token"),
                    @ApiResponse(responseCode = "403", description = "Invalid role")
            })
    @PostMapping(value = "register")
    @SuppressWarnings("unchecked cast")
    public HttpStatus registerUser(@RequestBody RegisterRequestDTO registerRequestDTO, HttpServletRequest request) {
        Set<Roles> userRoles = new HashSet<>((List<Roles>) request.getAttribute("roles"));

        authorisationService.authorize(userRoles, Roles.ADMIN);

        User user = userMapperService.mapToEntity(registerRequestDTO);

        userService.registerUser(user);

        return HttpStatus.OK;
    }

    @Operation(summary = "authenticate a user by username and password")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
                    @ApiResponse(responseCode = "401", description = "Invalid user credentials")
            })
    @PostMapping(value = "login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String username = loginRequestDTO.getUsername();
        String password = loginRequestDTO.getPassword();

        User user = userService.login(username, password);

        String token = TokenHandler.createToken(user.getUsername(), user.getCompanyIdentifier(), user.getRoles());

        return new LoginResponseDTO(token);
    }

    @Operation(summary = "logout logged in user")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully logged out"),
                    @ApiResponse(responseCode = "401", description = "Invalid token for already logged in user")
            })
    @PostMapping(value = "logout")
    public void logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        TokenHandler.validateToken(authorizationHeader);

        //TokenHandler.invalidateToken(jwt);
    }

}
