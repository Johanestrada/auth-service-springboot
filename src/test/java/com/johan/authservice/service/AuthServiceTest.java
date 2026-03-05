package com.johan.authservice.service;

import com.johan.authservice.entity.User;
import com.johan.authservice.exception.BusinessException;
import com.johan.authservice.repository.UserRepository;
import com.johan.authservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtService = Mockito.mock(JwtService.class);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }


    @Test
    void shouldThrowExceptionWhenPasswordIsTooshort() {
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register("test@test.com", "123");
        });
        assertEquals("La contraseña debe tener al menos 8 caracteres", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlReadyExists() {
        Mockito.when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register("test@test.com", "password123");
    }     );

        assertEquals("Correo electrónico ya registrado", exception.getMessage());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        Mockito.when(userRepository.existsByEmail("test@test.com")).thenReturn(false);

        Mockito.when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setEmail("test@test.com");

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        User  result = authService.register("test@test.com", "password123");

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());

    }

    @Test
    void login_passwordIncorrect() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        Mockito.when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));


        Mockito.when(passwordEncoder.matches("wrongPassword", "encodedPassword"))
                .thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.login("test@test.com", "password123")
        );

        assertEquals("Credenciales incorrectas", exception.getMessage());

        Mockito.verify(jwtService, Mockito.never()).generateToken(Mockito.any());
    }

    @Test
    void Login_success() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("encodedPassword");

        Mockito.when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        Mockito.when(passwordEncoder.matches("encodedPassword", "encodedPassword"))
                .thenReturn(true);

        Mockito.when(jwtService.generateToken(user))
                .thenReturn("jwtTokenFake");

        var response = authService.login("test@test.com", "encodedPassword");

        assertNotNull(response);
        assertEquals("jwtTokenFake", response.getToken());

        Mockito.verify(jwtService).generateToken(user);
    }

    @Test
    void user_not_exists() {
        Mockito.when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.empty());

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.login("test@gmail.com", "password123")
                );

        assertEquals("Usuario no encontrado!", exception.getMessage());

        Mockito.verify(passwordEncoder, Mockito.never())
                .matches(Mockito.any(), Mockito.any());
        Mockito.verify(jwtService, Mockito.never())
                .generateToken(Mockito.any());

    }

    @Test
    void register_short_password() {
        String email = "test@test.com";
        String shortPassword = "123";

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            authService.register(email, shortPassword);
        });

        assertEquals("La contraseña debe tener al menos 8 caracteres", exception.getMessage());

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());

        Mockito.verify(passwordEncoder, Mockito.never()).encode(Mockito.any());
    }

    @Test
    void duplicate_email_registration() {
        String email = "test@test.com";
        String password = "password123";

        Mockito.when(userRepository.existsByEmail(email))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.register(email, password)
        );

        assertEquals(
                "Correo electrónico ya registrado",
                exception.getMessage()
        );

        Mockito.verify(userRepository, Mockito.never())
                .save(Mockito.any());

        Mockito.verify(passwordEncoder, Mockito.never())
                .encode(Mockito.any());
    }
}
