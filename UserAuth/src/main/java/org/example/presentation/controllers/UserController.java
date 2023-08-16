package org.example.presentation.controllers;

import org.example.business.services.UserService;
import org.example.persistence.collections.User;
import org.example.presentation.utils.UserMapperService;
import org.example.presentation.view.LoginRequestDTO;
import org.example.presentation.view.RegisterRequestDTO;
import org.example.services.TokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200/")
public class UserController {
    private final UserService userService;
    private final UserMapperService userMapperService;

    private final TokenService tokenService;

    public UserController(UserService userService, UserMapperService userMapperService, TokenService tokenService) {
        this.userService = userService;
        this.userMapperService = userMapperService;
        this.tokenService = tokenService;
    }

    @PostMapping(value = "register")
    public HttpStatus registerUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        User user = userMapperService.mapToEntity(registerRequestDTO);

        userService.registerUser(user);

        return HttpStatus.OK;
    }

    @PostMapping(value = "login")
    public String login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String username = loginRequestDTO.getUsername();
        String password = loginRequestDTO.getPassword();

        User user = userService.login(username, password);

        return tokenService.createToken(user.getUsername(), user.getRoles());
    }

    @PostMapping(value = "validate")
    public String validate(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        return  tokenService.validateToken(authorizationHeader);
    }

}
