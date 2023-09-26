package org.example.business.services;

import org.example.business.utils.Password;
import org.example.customexceptions.AlreadyExistingUserException;
import org.example.customexceptions.InvalidCredentialsException;
import org.example.persistence.collections.User;
import org.example.persistence.repository.UserRepository;
import org.example.utils.ErrorMessages;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * This service is used for performing operations related to user access
 */
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * It creates new user, if another one with the same username doesn't exist
     *
     * @param user resource to be created
     * @throws AlreadyExistingUserException if a user with the same username already exists
     */
    public void registerUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            throw new AlreadyExistingUserException(ErrorMessages.ALREADY_EXISTING_USER, user.getUsername());
        }

        user.setIdentifier(UUID.randomUUID());
        userRepository.save(user);
    }

    /**
     * It generates a JWT if received credentials are valid for an existing user account
     *
     * @param username existing account's username
     * @param password existing account's password
     * @return an object representing the existing user account
     */
    public User login(String username, String password) {
        Optional<User> existingUser = userRepository.findByUsername(username);

        if (existingUser.isEmpty()) {
            throw new InvalidCredentialsException(ErrorMessages.INVALID_CREDENTIALS);
        } else {
            User user = existingUser.get();
            boolean isPasswordValid = Password.checkPassword(password, user.getPassword());

            if (!isPasswordValid) {
                throw new InvalidCredentialsException(ErrorMessages.INVALID_CREDENTIALS);
            }

            return user;
        }
    }
}
