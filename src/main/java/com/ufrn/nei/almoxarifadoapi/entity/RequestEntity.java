package com.ufrn.nei.almoxarifadoapi.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ufrn.nei.almoxarifadoapi.enums.RequestStatusEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Solicitações")
public class RequestEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private RequestStatusEnum status;

    @Column(name = "descrição", nullable = false)
    private String description;

    @Column(name = "criado_em", nullable = false)
    private Timestamp createdAt = Timestamp.valueOf(LocalDateTime.now());

    @Column(name = "atualizado_em", nullable = false)
    private Timestamp updatedAt = Timestamp.valueOf(LocalDateTime.now());

    @Column(name = "quantidade", nullable = false)
    private Long quantity;

    @Column(name = "comentário_admin")
    private String adminComment;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JsonManagedReference
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    public RequestEntity(RequestStatusEnum status, String description, Long quantity, UserEntity user, ItemEntity item) {
        this.status = status;
        this.description = description;
        this.quantity = quantity;
        this.user = user;
        this.item = item;
    }
}
