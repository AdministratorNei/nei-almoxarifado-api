package com.ufrn.nei.almoxarifadoapi.dto.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateDTO {
    private String name;
    @PositiveOrZero
    private Long sipacCode;
    @PositiveOrZero
    private Integer quantity;
    @PositiveOrZero
    private Integer minimumStockLevel;
    private String type;
}
