package com.system.automation.business.services;

import com.system.automation.business.errorhandling.ErrorMessages;
import com.system.automation.business.errorhandling.customexceptions.AlreadyExistingUserException;
import com.system.automation.business.errorhandling.customexceptions.UserNotFoundException;
import com.system.automation.persistence.collections.User;
import com.system.automation.persistence.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(String username){
        return userRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException(ErrorMessages.USER_NOT_FOUND, username));
    }

    public void registerUser(User user){
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());

        if(existingUser.isPresent()){
            throw new AlreadyExistingUserException(ErrorMessages.ALREADY_EXISTING_USER, user.getUsername());
        }

        userRepository.save(user);
    }
}
