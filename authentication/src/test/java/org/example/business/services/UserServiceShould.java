package org.example.business.services;


import org.example.business.utils.Password;
import org.example.customexceptions.AlreadyExistingResourceException;
import org.example.customexceptions.InvalidCredentialsException;
import org.example.persistence.collections.User;
import org.example.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceShould {

    @Mock
    UserRepository userRepository;

    MockedStatic<Password> passwordMockedStatic;

    UserService userService;

    @Before
    public void initialize() {
        userService = new UserService(userRepository);
    }

    @Test
    public void throwExceptionWhenTryingToRegisterExistingUser() {
        String existingUsername = "Random name";
        User newUser = createUser(existingUsername);

        given(userRepository.findByUsername(existingUsername)).willReturn(Optional.of(User.builder().build()));

        assertThrows(AlreadyExistingResourceException.class, () -> userService.registerUser(newUser));

        verify(userRepository).findByUsername(existingUsername);
    }

    @Test
    public void throwInvalidCredentialsExceptionWhenTryingToLoginNonExistingUser() {
        String username = "Random username";
        String password = "Random password";

        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.login(username, password));

        verify(userRepository).findByUsername(username);
    }

    @Test
    public void throwInvalidCredentialsExceptionWhenTryingToLoginWithInvalidPassword() {
        passwordMockedStatic = Mockito.mockStatic(Password.class);
        String username = "Random username";
        String password = "Random password";
        User user = createUser(username);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(User.builder().build()));
        passwordMockedStatic.when(() -> Password.checkPassword(password, user.getPassword())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> userService.login(username, password));

        verify(userRepository).findByUsername(username);
        passwordMockedStatic.verify(()-> Password.checkPassword(password, user.getPassword()));
    }

    private User createUser(String username) {
        return User.builder()
                .username(username)
                .build();
    }
}