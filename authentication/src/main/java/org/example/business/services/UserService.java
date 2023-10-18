package org.example.business.services;

import org.example.business.utils.Password;
import org.example.customexceptions.AlreadyExistingResourceException;
import org.example.customexceptions.InvalidCredentialsException;
import org.example.customexceptions.ResourceNotFoundException;
import org.example.persistence.collections.User;
import org.example.persistence.repository.UserRepository;
import org.example.utils.ErrorMessages;
import org.springframework.stereotype.Service;

import java.util.List;
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
     * @throws AlreadyExistingResourceException if a user with the same username already exists
     */
    public void registerUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if (existingUser.isPresent()) {
            throw new AlreadyExistingResourceException(ErrorMessages.ALREADY_EXISTING_USER, user.getUsername());
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

    /**
     * It searches all existing users
     *
     * @return the list of existing users
     */
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    /**
     * It removes the user identified by the given UUID from existing user list
     *
     * @param identifier user UUID
     */
    public void deleteUser(UUID identifier) {
        int deletedRowsCount = this.userRepository.customDeleteById(identifier);

        if (deletedRowsCount == 0) {
            throw new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND, identifier.toString());
        }
    }
}
