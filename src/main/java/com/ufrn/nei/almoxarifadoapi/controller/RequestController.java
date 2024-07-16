package com.ufrn.nei.almoxarifadoapi.controller;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.PageableMapper;
import com.ufrn.nei.almoxarifadoapi.dto.mapper.RequestMapper;
import com.ufrn.nei.almoxarifadoapi.dto.pageable.PageableDTO;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordResponseDTO;
import com.ufrn.nei.almoxarifadoapi.dto.request.RequestAdminCommentDTO;
import com.ufrn.nei.almoxarifadoapi.dto.request.RequestCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.request.RequestResponseDTO;
import com.ufrn.nei.almoxarifadoapi.entity.RequestEntity;
import com.ufrn.nei.almoxarifadoapi.enums.RequestStatusEnum;
import com.ufrn.nei.almoxarifadoapi.exception.StatusNotFoundException;
import com.ufrn.nei.almoxarifadoapi.infra.RestErrorMessage;
import com.ufrn.nei.almoxarifadoapi.infra.jwt.JwtUserDetails;
import com.ufrn.nei.almoxarifadoapi.repository.projection.RequestProjection;
import com.ufrn.nei.almoxarifadoapi.service.RequestService;
import io.jsonwebtoken.Jwt;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Tag(name = "Solicitações", description = "Contém todas as operações relativas aos recursos para aceitar, excluir, cancelar, criar e ler uma solicitação")
@RestController
@RequestMapping("/api/v1/requests")
public class RequestController {
    @Autowired
    private RequestService requestService;

    @Operation(summary = "Criar solicitação.",
            description = "Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN','USER'.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Solicitação criada com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RequestResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Erro ao encontrar item solicitado.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<RequestResponseDTO> create(@RequestBody @Valid RequestCreateDTO data) {
        RequestEntity request = requestService.create(data);
        RequestResponseDTO response = RequestMapper.toResponseDTO(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Aceitar solicitação.",
            description = "Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Solicitação aceita com sucesso.",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Erro ao aceitar solicitação",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Usuário não está autenticado ou sem permissão de acesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Erro ao encontrar solicitação.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Erro ao aceitar. Solicitação não consta como pendente ou já foi aceita anteriormente.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "500", description = "Erro aceitar solicitação. Quantidade de itens disponiveis insuficientes.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
            })
    @PatchMapping("/accept/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> accept(@PathVariable Long id, @RequestBody(required = false) RequestAdminCommentDTO adminCommentDTO) {
        Boolean request = requestService.accept(id, adminCommentDTO);

        if (request == Boolean.TRUE) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Recusar solicitação.",
            description = "Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Solicitação recusada com sucesso.",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Erro ao recusar solicitação.",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Usuário não está autenticado ou sem permissão de acesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Erro ao encontrar solicitação.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Erro ao recusar. Solicitação não consta como pendente ou já foi recusada anteriormente.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
    })
    @PatchMapping("/decline/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> decline(@PathVariable Long id, @RequestBody(required = false) RequestAdminCommentDTO adminCommentDTO) {
        Boolean request = requestService.decline(id, adminCommentDTO);

        if (request == Boolean.TRUE) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Cancelar solicitação.",
            description = "Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN','USER'.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Solicitação cancelada com sucesso.",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Erro ao cancelar solicitação.",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Usuário não está autenticado ou sem permissão de acesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Erro ao encontrar solicitação.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "Erro ao cancelar. Solicitação não consta como pendente ou já foi recusada anteriormente.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            })
    @PatchMapping("/cancel/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        Boolean request = requestService.cancel(id);

        if (request == Boolean.TRUE) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Recuperar todas solicitações",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN','USER'",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa a página retornada"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "id,asc")),
                            description = "Representa a ordenação dos resultados. Aceita múltiplos critérios de ordenação"
                    ),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "applicaton/json;charset=UTF-8", schema = @Schema(implementation = PageableDTO.class)))
            })
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PageableDTO> findAll(Pageable pageable,
                                               @AuthenticationPrincipal JwtUserDetails userDetails) {
        Page<RequestProjection> requestPage = requestService.findAll(userDetails, pageable);
        PageableDTO response = PageableMapper.toDto(requestPage);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Recuperar solicitação pelo ID",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN','USER'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "applicaton/json;charset=UTF-8", schema = @Schema(implementation = RequestResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Solicitação não foi encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<RequestResponseDTO> findById(@PathVariable Long id) {
        RequestEntity request = requestService.findById(id);

        RequestResponseDTO response = RequestMapper.toResponseDTO(request);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Recuperar solicitação pelo Status",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN','USER'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "applicaton/json;charset=UTF-8", schema = @Schema(implementation = PageableDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Solicitação não foi encontrada",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            })
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PageableDTO> findByStatus(@PathVariable String status,
                                                    Pageable pageable,
                                                    @AuthenticationPrincipal JwtUserDetails userDetails) {
        Page<RequestProjection> requestPage = requestService.findByStatus(userDetails, status, pageable);
        PageableDTO response = PageableMapper.toDto(requestPage);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Recuperar solicitação pelo ID de usuário",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN','USER'",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "userId",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Seleciona o usuário desejado"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa a página retornada"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                            array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "id,asc")),
                            description = "Representa a ordenação dos resultados. Aceita múltiplos critérios de ordenação"
                    ),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "applicaton/json;charset=UTF-8", schema = @Schema(implementation = PageableDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            })
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PageableDTO> findByUserId(@RequestParam(required = false, defaultValue = "0") Integer userId,
                                                    Pageable pageable,
                                                    @AuthenticationPrincipal JwtUserDetails userDetails) {
        Page<RequestProjection> requestPage = requestService.findByUserID(userId, userDetails, pageable);
        PageableDTO response = PageableMapper.toDto(requestPage);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Recuperar solicitação pelo ID do item",
            description = "Requisição exige uso de um bearer token. Acesso restrito a Role='ADMIN','USER'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "applicaton/json;charset=UTF-8", schema = @Schema(implementation = PageableDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            })
    @GetMapping("/item/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PageableDTO> findByItemId(@PathVariable Long id,
                                                    Pageable pageable,
                                                    @AuthenticationPrincipal JwtUserDetails userDetails) {
        Page<RequestProjection> requestPage = requestService.findByItemID(id, userDetails,pageable);
        PageableDTO response = PageableMapper.toDto(requestPage);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
