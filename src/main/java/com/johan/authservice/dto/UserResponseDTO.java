package com.johan.authservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private LocalDateTime createAt;
    private boolean enabled;
}
