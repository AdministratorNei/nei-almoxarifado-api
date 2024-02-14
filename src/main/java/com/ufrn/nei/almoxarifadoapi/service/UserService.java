package com.ufrn.nei.almoxarifadoapi.service;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.RoleMapper;
import com.ufrn.nei.almoxarifadoapi.dto.mapper.UserMapper;
import com.ufrn.nei.almoxarifadoapi.dto.role.RoleResponseDto;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserResponseDTO;
import com.ufrn.nei.almoxarifadoapi.entity.UserEntity;
import com.ufrn.nei.almoxarifadoapi.exception.EntityNotFoundException;
import com.ufrn.nei.almoxarifadoapi.exception.PasswordInvalidException;
import com.ufrn.nei.almoxarifadoapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Transactional
    public UserResponseDTO save(UserCreateDTO createDTO) {
        UserEntity user = UserMapper.toItem(createDTO);
        RoleResponseDto roleResponse = roleService.findById(createDTO.getRoleId());

        if (roleResponse != null) {
            user.setRole(RoleMapper.toRole(roleResponse));
        }

        user = userRepository.save(user);

        return UserMapper.toResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
       return userRepository.findById(id).orElseThrow(
               () -> new EntityNotFoundException(String.format("Usuário não encontrado com id='%s'", id)
       ));
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findByEmail(String email) {
        return UserMapper.toItem(userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário não encontrado com email='%s'", email))
        ));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return UserMapper.toListResponseDTO(userRepository.findAll());
    }

    @Transactional
    public void updatePassword(String currentPassword, String newPassword, String confirmPassword, Long id) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordInvalidException("Nova senha não é igual a confirma senha");
        }

        UserEntity user = findById(id);

        if (!currentPassword.equals(user.getPassword())) {
            throw new PasswordInvalidException("Senha atual é inválida");
        }

        user.setPassword(newPassword);
    }

    @Transactional
    public void deleteUser(Long id) {
        UserEntity user = findById(id);

        if (!user.getActive()) {
            throw new EntityNotFoundException("O usuário não existe.");
        }

        user.setActive(false);
    }
}
