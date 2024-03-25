package com.ufrn.nei.almoxarifadoapi.repository;

import com.ufrn.nei.almoxarifadoapi.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<RecordEntity, Long> {
    @Query("SELECT ue FROM RecordEntity ue WHERE " +
            "(ue.user.id = :id) OR " +
            "LOWER(ue.user.name) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
            "LOWER(ue.user.email) LIKE LOWER(CONCAT('%', :email, '%')) OR " +
            "LOWER(ue.user.role.role) LIKE LOWER(CONCAT('%', :role, '%'))")
    List<RecordEntity> findByUsers(Long id, String name, String email, String role);

    @Query("SELECT ue FROM RecordEntity ue WHERE " +
            "(ue.item.id = :id) OR " +
            "CAST(ue.item.sipacCode AS string) LIKE CONCAT('%', CAST(:sipacCode AS string), '%') OR " +
            "LOWER(ue.item.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<RecordEntity> findByItens(Long id, Long sipacCode, String name);
}
