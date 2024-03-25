package com.ufrn.nei.almoxarifadoapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "itens")
public class ItemEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nome", nullable = false, unique = false, length = 255)
    private String name;

    @Column(name = "codigo_sipac", nullable = false, unique = true)
    private Long sipacCode;

    @Column(name = "quantidade", nullable = false)
    private int quantity;

    @Column(name = "tipo_unitario")
    private String type;

    @Column(name = "criado_em", nullable = false)
    private Timestamp createdAt = Timestamp.valueOf(LocalDateTime.now());

    @Column(name = "atualizado_em", nullable = false)
    private Timestamp updatedAt = Timestamp.valueOf(LocalDateTime.now());

    @Column(name = "disponivel", nullable = false)
    private Boolean available = true;

    @OneToMany(mappedBy = "item")
    private List<RecordEntity> records;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemEntity that = (ItemEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
                && Objects.equals(sipacCode, that.sipacCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sipacCode);
    }

    @Override
    public String toString() {
        return "ItemEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sipacCode=" + sipacCode +
                '}';
    }
}
