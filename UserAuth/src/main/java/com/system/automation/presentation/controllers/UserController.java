package com.system.automation.presentation.controllers;

import com.system.automation.business.services.UserService;
import com.system.automation.persistence.collections.User;
import com.system.automation.presentation.utils.MapperService;
import com.system.automation.presentation.view.RegisterRequestDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200/")
public class UserController {
    private final UserService userService;
    private final MapperService mapperService;

    public UserController(UserService userService, MapperService mapperService) {
        this.userService = userService;
        this.mapperService = mapperService;
    }

    @PostMapping
    public HttpStatus registerUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        User user = mapperService.mapToEntity(registerRequestDTO);

        userService.registerUser(user);

        return HttpStatus.OK;
    }

    @GetMapping
    public HttpStatus registerUser() {
        return HttpStatus.OK;
    }
}
