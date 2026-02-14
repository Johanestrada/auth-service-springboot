package com.johan.authservice.mapper;

import com.johan.authservice.dto.UserResponseDTO;
import com.johan.authservice.entity.User;

public class UserMapper {

    public static UserResponseDTO toDto(User user){
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setCreateAt(user.getCreateAt());
        dto.setEnabled(user.isEnabled());
        return dto;
    }

}
