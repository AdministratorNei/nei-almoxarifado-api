package com.ufrn.nei.almoxarifadoapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ufrn.nei.almoxarifadoapi.entity.ItemEntity;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    List<ItemEntity> findAllByAvailableTrue();

    Optional<ItemEntity> findBySipacCode(Long sipacCode);

    Optional<ItemEntity> findByName(String name);
}
