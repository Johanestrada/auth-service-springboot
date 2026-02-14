package com.johan.authservice.service;

import com.johan.authservice.dto.AuthResponseDTO;
import com.johan.authservice.entity.User;
import com.johan.authservice.mapper.UserMapper;
import com.johan.authservice.repository.UserRepository;
import com.johan.authservice.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    public AuthResponseDTO login(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Usuario no encontrado!"));

        if (!passwordEncoder.matches(password,user.getPassword())){
            throw new RuntimeException("Credenciales incorrectas");
        }
        String token = jwtService.generateToken(user);
        return new AuthResponseDTO(token);
    }

}
