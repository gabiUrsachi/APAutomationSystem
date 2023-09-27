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
import org.example.presentation.view.UserDTO;
import org.example.services.AuthorisationService;
import org.example.utils.AuthorizationMapper;
import org.example.utils.data.JwtClaims;
import org.example.utils.data.Roles;
import org.example.utils.TokenHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
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
                    @ApiResponse(responseCode = "403", description = "Invalid role"),
                    @ApiResponse(responseCode = "409", description = "Already existing user")
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


    @Operation(summary = "get all users")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Found users"),
                    @ApiResponse(responseCode = "401", description = "Invalid token"),
                    @ApiResponse(responseCode = "403", description = "Invalid role")
            })
    @GetMapping
    @SuppressWarnings("unchecked cast")
    public List<UserDTO> getUsers(HttpServletRequest request) {
        Set<Roles> userRoles = new HashSet<>((List<Roles>) request.getAttribute("roles"));

        authorisationService.authorize(userRoles, Roles.ADMIN);

        List<User> users = userService.getAllUsers();

        return userMapperService.mapToDTO(users);
    }

    @Operation(summary = "remove user by id")
    @ApiResponses(value =
            {
                    @ApiResponse(responseCode = "200", description = "Successfully removed user"),
                    @ApiResponse(responseCode = "401", description = "Invalid token"),
                    @ApiResponse(responseCode = "403", description = "Invalid role"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            })
    @DeleteMapping("{identifier}")
    @SuppressWarnings("unchecked cast")
    public void deleteUser(@PathVariable UUID identifier, HttpServletRequest request) {
        Set<Roles> userRoles = new HashSet<>((List<Roles>) request.getAttribute("roles"));

        authorisationService.authorize(userRoles, Roles.ADMIN);

        userService.deleteUser(identifier);
    }
}
