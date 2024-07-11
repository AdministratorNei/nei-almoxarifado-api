package com.ufrn.nei.almoxarifadoapi.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpdateDto {
    @NotBlank
    private String newRole;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleUpdateDto that = (RoleUpdateDto) o;
        return Objects.equals(newRole, that.newRole);
    }

    @Override
    public int hashCode() {
        return Objects.hash(newRole);
    }

    @Override
    public String toString() {
        return "RoleUpdateDto{" +
                "newRole='" + newRole + '\'' +
                '}';
    }
}
