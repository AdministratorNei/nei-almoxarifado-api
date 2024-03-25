package com.ufrn.nei.almoxarifadoapi.service;

import com.ufrn.nei.almoxarifadoapi.dto.role.RoleUpdateDto;
import com.ufrn.nei.almoxarifadoapi.entity.RoleEntity;
import com.ufrn.nei.almoxarifadoapi.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public RoleEntity save(RoleEntity roleEntity) {
        RoleEntity role = roleRepository.save(roleEntity);
        return role;
    }

    @Transactional(readOnly = true)
    public RoleEntity findById(Long id) {
        RoleEntity role = roleRepository.findById(id).orElseThrow(
                () -> new RuntimeException(String.format("Role não encontrada com o id: %d", id)));
        return role;
    }

    @Transactional(readOnly = true)
    public List<RoleEntity> findAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public Boolean deleteById(Long id) {
        try {
            if (roleRepository.existsById(id)) {
                roleRepository.deleteById(id);
                return true;
            } else {
                log.warn("Tentativa de excluir um registro inexistente com ID: {}", id);
                return false;
            }
        } catch (IllegalArgumentException ex) {
            log.error("Erro ao excluir registro com ID: {}", id, ex);
            throw new IllegalArgumentException("Erro ao excluir registro.", ex);
        }
    }

    @Transactional
    public RoleEntity updateRoleById(Long id, RoleUpdateDto newRole) {
        RoleEntity role = findById(id);
        role.setRole(newRole.getNewRole());
        save(role);

        return role;
    }
}