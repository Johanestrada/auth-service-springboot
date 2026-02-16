package com.johan.authservice.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @PreAuthorize("HasRole('USER')")
    @GetMapping("/user/me")
    public String Me(Authentication authentication) {
        return "Usuario autenticado: " + authentication.getName();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/panel")
    public String admin() {
        return "Solo admins pueden acceder a este panel";
    }

}
