package com.ufrn.nei.almoxarifadoapi.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// DTO que vai conter a resposta do ADMIN a uma solicitação
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestAdminCommentDTO {
    private String comment;
}
