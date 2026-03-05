package com.johan.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para cambiar email")
public class ChangeEmailDTO {

    @NotBlank(message = "El nuevo email es requerido")
    @Email(message = "Debe ser un correo válido")
    @Schema(description = "Nuevo correo electrónico", example = "nuevo@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newEmail;
}

