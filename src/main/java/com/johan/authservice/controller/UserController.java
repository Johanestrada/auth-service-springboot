package com.johan.authservice.controller;

import com.johan.authservice.dto.ChangeEmailDTO;
import com.johan.authservice.dto.ChangePasswordDTO;
import com.johan.authservice.dto.UserResponseDTO;
import com.johan.authservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Obtener usuario actual", description = "Devuelve el email del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public Map<String, String> me(Authentication authentication) {

        if (authentication == null) {
            return Map.of("email", "anonymous");
        }

        return Map.of(
                "email", authentication.getName()
        );
    }

    @GetMapping("/user/profile")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Obtener perfil del usuario", description = "Devuelve los datos completos del perfil del usuario autenticado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil obtenido correctamente"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponseDTO> getProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        String email = authentication.getName();
        UserResponseDTO profile = userService.getUserByEmail(email);

        return ResponseEntity.ok(profile);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/panel")
    @Operation(summary = "Panel de administración", description = "Acceso solo para administradores")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Acceso permitido"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - requiere rol ADMIN")
    })
    @SecurityRequirement(name = "bearerAuth")
    public String admin() {
        return "Solo admins pueden acceder a este panel";
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los usuarios", description = "Obtiene el listado completo de usuarios (Solo ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene los detalles de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PatchMapping("/users/{id}/password")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Cambiar contraseña", description = "Permite al usuario cambiar su propia contraseña")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contraseña actualizada"),
            @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta o nueva contraseña inválida"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponseDTO> changePassword(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long id,
            @Valid @RequestBody ChangePasswordDTO request) {

        // Validar que las contraseñas coincidan
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(userService.changePassword(id, request.getOldPassword(), request.getNewPassword()));
    }

    @PatchMapping("/users/{id}/email")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Cambiar email", description = "Permite al usuario cambiar su correo electrónico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email actualizado"),
            @ApiResponse(responseCode = "400", description = "Email ya registrado o inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponseDTO> changeEmail(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long id,
            @Valid @RequestBody ChangeEmailDTO request) {
        return ResponseEntity.ok(userService.changeEmail(id, request.getNewEmail()));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema (Solo ADMIN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activar/Desactivar usuario", description = "Cambia el estado habilitado del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UserResponseDTO> toggleUserStatus(
            @Parameter(description = "ID del usuario", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }

}
