package com.ufrn.nei.almoxarifadoapi.repository;

import com.ufrn.nei.almoxarifadoapi.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRole(String role);
}
