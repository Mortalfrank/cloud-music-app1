package com.musicapp.service;

import com.musicapp.model.User;
import com.musicapp.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    private final UserRepository userRepository;

    public RegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean registerUser(String email, String username, String password) {
        // Check whether the mailbox already exists
        if (userRepository.findByEmail(email) != null) {
            return false;
        }

        // Create and save new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUserName(username);
        newUser.setPassword(password);

        userRepository.save(newUser);
        return true;
    }
}