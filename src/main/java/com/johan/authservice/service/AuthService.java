package com.johan.authservice.service;

import com.johan.authservice.dto.AuthResponseDTO;
import com.johan.authservice.entity.User;
import com.johan.authservice.exception.BusinessException;
import com.johan.authservice.repository.UserRepository;
import com.johan.authservice.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
import com.johan.authservice.security.Role;
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

    public User register(String email, String password) {

        if (password == null || password.length() < 8) {
            throw new BusinessException("La contraseña debe tener al menos 8 caracteres");
        }

        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Correo electrónico ya registrado");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreateAt(LocalDateTime.now());
        user.setEnabled(true);
        user.setRoles(Set.of(Role.ROLE_USER));

        return userRepository.save(user);
    }

    public AuthResponseDTO login(String email, String password){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BusinessException("Usuario no encontrado!"));

        if (!passwordEncoder.matches(password,user.getPassword())){
            throw new BusinessException("Credenciales incorrectas");
        }
        String token = jwtService.generateToken(user);
        return new AuthResponseDTO(token);
    }

    public AuthResponseDTO refreshToken(String token) {
        try {
            // Extraer el email del token actual
            String email = jwtService.extractEmail(token);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

            // Generar un nuevo token
            String newToken = jwtService.generateToken(user);
            return new AuthResponseDTO(newToken);
        } catch (Exception e) {
            throw new BusinessException("Token inválido o expirado");
        }
    }

}

