package com.johan.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para cambiar contraseña")
public class ChangePasswordDTO {

    @NotBlank(message = "La contraseña actual es requerida")
    @Schema(description = "Contraseña actual", example = "password123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;

    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Schema(description = "Nueva contraseña", example = "newPassword456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;

    @NotBlank(message = "La confirmación de contraseña es requerida")
    @Schema(description = "Confirmación de nueva contraseña", example = "newPassword456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;
}

