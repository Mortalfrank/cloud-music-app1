package com.musicapp.service;

import com.musicapp.model.User;
import com.musicapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public User validateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;  // Returns the entire user object
        }
        return null;
    }
}