package com.ufrn.nei.almoxarifadoapi.dto.request;

import com.ufrn.nei.almoxarifadoapi.dto.item.ItemResponseDTO;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserResponseDTO;
import com.ufrn.nei.almoxarifadoapi.enums.RequestStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponseDTO {
    private Long id;
    private ItemResponseDTO item;
    private Long quantity;
    private RequestStatusEnum status;
    private String description;
    private UserResponseDTO user;
    private String createdAt;
    private String updatedAt;
    private String adminComment;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestResponseDTO that = (RequestResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(item, that.item) && Objects.equals(quantity, that.quantity) && status == that.status && Objects.equals(description, that.description) && Objects.equals(user, that.user) && Objects.equals(createdAt, that.createdAt) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, quantity, status, description, user, createdAt, updatedAt);
    }

    @Override
    public String toString() {
        return "RequestResponseDTO{" +
                "id=" + id +
                ", item=" + item +
                ", quantity=" + quantity +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", user=" + user +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
