package org.example.business.services;

import org.example.business.utils.Password;

import org.example.errorhandling.customexceptions.AlreadyExistingUserException;
import org.example.errorhandling.customexceptions.InvalidCredentialsException;
import org.example.errorhandling.customexceptions.UserNotFoundException;
import org.example.errorhandling.utils.ErrorMessages;
import org.example.persistence.collections.User;
import org.example.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String username){
        return userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException(org.example.errorhandling.utils.ErrorMessages.USER_NOT_FOUND, username));
    }

    public void registerUser(User user){
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if(existingUser.isPresent()){
            throw new AlreadyExistingUserException(ErrorMessages.ALREADY_EXISTING_USER, user.getUsername());
        }

        user.setIdentifier(UUID.randomUUID());
        userRepository.save(user);
    }

    public User login(String username, String password){
        Optional<User> existingUser = userRepository.findByUsername(username);

        if(existingUser.isEmpty()){
            throw new UserNotFoundException(ErrorMessages.USER_NOT_FOUND, username);
        }
        else{
            User user = existingUser.get();
            boolean isPasswordValid = Password.checkPassword(password, user.getPassword());

            if(!isPasswordValid){
                throw new InvalidCredentialsException(ErrorMessages.INVALID_CREDENTIALS);
            }

            return user;
        }
    }
}
