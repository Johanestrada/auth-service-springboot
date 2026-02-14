package com.johan.authservice.service;

import com.johan.authservice.entity.User;
import com.johan.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String email, String password){
        if (userRepository.existsByEmail(email)){
            throw new RuntimeException("Correo electrónico ya registrado");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreateAt(LocalDateTime.now());
        user.setEnabled(true);
        return userRepository.save(user);
    }
}
