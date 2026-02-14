package com.johan.authservice.controller;

import com.johan.authservice.dto.AuthResponseDTO;
import com.johan.authservice.dto.LoginRequestDTO;
import com.johan.authservice.dto.RegisterRequestDTO;
import com.johan.authservice.entity.User;
import com.johan.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody RegisterRequestDTO dto){
        return authService.register(dto.getEmail(), dto.getPassword());
    }

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody LoginRequestDTO dto) {
        return authService.login(dto.getEmail(), dto.getPassword());
    }




    @GetMapping("/")
    public String home() {
        return "Auth Service OK 🚀";
    }
}
