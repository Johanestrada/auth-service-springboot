package com.johan.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request de autenticación (login)")
public class AuthRequestDTO {

    @Schema(description = "Correo electrónico del usuario", example = "correo@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "Contraseña del usuario", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    // Lombok genera getters/setters, constructor sin-args y all-args
}
