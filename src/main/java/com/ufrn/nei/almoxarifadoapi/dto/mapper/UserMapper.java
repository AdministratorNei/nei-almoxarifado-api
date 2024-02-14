package com.ufrn.nei.almoxarifadoapi.dto.mapper;

import com.ufrn.nei.almoxarifadoapi.dto.user.UserCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserResponseDTO;
import com.ufrn.nei.almoxarifadoapi.entity.UserEntity;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserResponseDTO toResponseDTO(UserEntity userEntity) {
        return new ModelMapper().map(userEntity, UserResponseDTO.class);
    }

    public static UserEntity toItem(UserCreateDTO userDTO) {
        return new ModelMapper().map(userDTO, UserEntity.class);
    }

    public static UserEntity toItem(UserResponseDTO userDTO) {
        return new ModelMapper().map(userDTO, UserEntity.class);
    }


    public static UserResponseDTO toItem(UserEntity userEntity) {
        return new ModelMapper().map(userEntity, UserResponseDTO.class);
    }

    public static List<UserResponseDTO> toListResponseDTO(List<UserEntity> users) {
        return users.stream().map(i -> toResponseDTO(i)).collect(Collectors.toList());
    }
}
