package com.johan.authservice.service;

import com.johan.authservice.dto.UserResponseDTO;
import com.johan.authservice.entity.User;
import com.johan.authservice.exception.BusinessException;
import com.johan.authservice.mapper.UserMapper;
import com.johan.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Obtener todos los usuarios (solo para ADMIN)
     */
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Obtener usuario por ID
     */
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        return UserMapper.toDto(user);
    }

    /**
     * Obtener usuario por email
     */
    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        return UserMapper.toDto(user);
    }

    /**
     * Actualizar contraseña de un usuario
     */
    public UserResponseDTO changePassword(Long id, String oldPassword, String newPassword) {
        if (newPassword == null || newPassword.length() < 8) {
            throw new BusinessException("La contraseña debe tener al menos 8 caracteres");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Contraseña actual incorrecta");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        return UserMapper.toDto(userRepository.save(user));
    }

    /**
     * Actualizar email de un usuario
     */
    public UserResponseDTO changeEmail(Long id, String newEmail) {
        if (userRepository.existsByEmail(newEmail)) {
            throw new BusinessException("El correo ya está registrado");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));

        user.setEmail(newEmail);
        return UserMapper.toDto(userRepository.save(user));
    }

    /**
     * Eliminar usuario
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        userRepository.delete(user);
    }

    /**
     * Habilitar/Deshabilitar usuario
     */
    public UserResponseDTO toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuario no encontrado"));
        user.setEnabled(!user.isEnabled());
        return UserMapper.toDto(userRepository.save(user));
    }
}

