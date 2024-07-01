package com.ufrn.nei.almoxarifadoapi.dto.item;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ItemUpdateIdealAmountDTO {
    @NotNull
    @PositiveOrZero
    private Integer idealAmount;
}
