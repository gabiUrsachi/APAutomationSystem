package org.example.presentation.controllers;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.Roles;
import org.example.business.services.UserService;
import org.example.persistence.collections.User;
import org.example.presentation.utils.UserMapperService;
import org.example.presentation.view.LoginRequestDTO;
import org.example.presentation.view.RegisterRequestDTO;
import org.example.utils.TokenHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200/")
public class UserController {
    private final UserService userService;
    private final UserMapperService userMapperService;


    public UserController(UserService userService, UserMapperService userMapperService) {
        this.userService = userService;
        this.userMapperService = userMapperService;
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

        return TokenHandler.createToken(user.getUsername(), user.getRoles());
    }

    @PostMapping(value = "logout")
    public void logout(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader){
        DecodedJWT jwt = TokenHandler.validateToken(authorizationHeader);

        TokenHandler.invalidateToken(jwt);
    }

    @PostMapping(value = "validate")
    public Set<Roles> validate(@RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        DecodedJWT jwt = TokenHandler.validateToken(authorizationHeader);

        return  TokenHandler.getRolesFromToken(jwt);
    }

}
