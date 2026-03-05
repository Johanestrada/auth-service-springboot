package com.johan.authservice.controller;

import com.johan.authservice.dto.AuthRequestDTO;
import com.johan.authservice.dto.AuthResponseDTO;
import com.johan.authservice.dto.RefreshTokenDTO;
import com.johan.authservice.dto.RegisterRequestDTO;
import com.johan.authservice.dto.UserResponseDTO;
import com.johan.authservice.entity.User;
import com.johan.authservice.mapper.UserMapper;
import com.johan.authservice.repository.UserRepository;
import com.johan.authservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthService authService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Registrar un nuevo usuario", description = "Registra un usuario con email y contraseña y devuelve el usuario creado (sin contraseña)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado"),
            @ApiResponse(responseCode = "400", description = "Petición inválida")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO register(@Valid @RequestBody RegisterRequestDTO dto){
        return UserMapper.toDto(
                authService.register(dto.getEmail(), dto.getPassword())
        );
    }


    @Operation(summary = "Login", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login correcto, devuelve token"),
            @ApiResponse(responseCode = "400", description = "Credenciales incorrectas o usuario no encontrado")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequestDTO request) {

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "El correo no existe"));
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Contraseña incorrecta"));
        }

        AuthResponseDTO authResponse = authService.login(user.getEmail(), request.getPassword());

        return ResponseEntity.ok(Map.of("token", authResponse.getToken()));
    }



    @GetMapping("/")
    @Operation(summary = "Health check del servicio", description = "Verifica que el servicio de autenticación esté disponible")
    @ApiResponse(responseCode = "200", description = "Servicio disponible")
    public String home() {
        return "Auth Service OK 🚀";
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token JWT", description = "Genera un nuevo token JWT válido usando un token actual")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Token inválido o expirado")
    })
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenDTO request) {
        try {
            AuthResponseDTO newToken = authService.refreshToken(request.getToken());
            return ResponseEntity.ok(Map.of("token", newToken.getToken()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token inválido o expirado"));
        }
    }

}
