package com.ufrn.nei.almoxarifadoapi.service;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.UserMapper;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserCreateDTO;
import com.ufrn.nei.almoxarifadoapi.entity.RoleEntity;
import com.ufrn.nei.almoxarifadoapi.entity.UserEntity;
import com.ufrn.nei.almoxarifadoapi.exception.EntityNotFoundException;
import com.ufrn.nei.almoxarifadoapi.exception.PasswordInvalidException;
import com.ufrn.nei.almoxarifadoapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity save(UserCreateDTO createDTO) {
        UserEntity user = UserMapper.toUser(createDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        RoleEntity role = roleService.findById(createDTO.getRoleId());

        if (role != null) {
            user.setRole(role);
        }

        userRepository.save(user);

        return user;
    }

    @Transactional(readOnly = true)
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário não encontrado com id='%s'", id)));
    }

    @Transactional(readOnly = true)
    public UserEntity findByEmail(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário não encontrado com email='%s'", email)));

        return user;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void updatePassword(String currentPassword, String newPassword, String confirmPassword, Long id) {
        if (!newPassword.equals(confirmPassword)) {
            throw new PasswordInvalidException("Nova senha não é igual a confirma senha");
        }

        UserEntity user = findById(id);

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new PasswordInvalidException("Senha atual é inválida");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
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
