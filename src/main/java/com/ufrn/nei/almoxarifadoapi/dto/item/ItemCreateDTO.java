package com.ufrn.nei.almoxarifadoapi.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemCreateDTO {
    @NotBlank
    private String name;
    @NotNull
    @Positive
    private int quantity;
    @PositiveOrZero
    private Integer idealAmount = 0;
    @NotBlank
    private String type;
    @PositiveOrZero
    private Long sipacCode;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ItemCreateDTO that = (ItemCreateDTO) o;
        return Objects.equals(name, that.name) && Objects.equals(sipacCode, that.sipacCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sipacCode);
    }

    @Override
    public String toString() {
        return "ItemCreateDTO{" +
                "name='" + name + '\'' +
                ", sipacCode=" + sipacCode +
                '}';
    }
}
