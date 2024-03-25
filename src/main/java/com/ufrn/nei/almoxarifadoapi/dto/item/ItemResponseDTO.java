package com.ufrn.nei.almoxarifadoapi.dto.item;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDTO {
    private Long id;
    private String name;
    private Long sipacCode;
    private int quantity;
    private String type;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean available;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemResponseDTO that = (ItemResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name)
                && Objects.equals(sipacCode, that.sipacCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, sipacCode);
    }

    @Override
    public String toString() {
        return "ItemResponseDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sipacCode=" + sipacCode +
                '}';
    }
}
