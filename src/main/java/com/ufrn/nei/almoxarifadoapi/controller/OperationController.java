package com.ufrn.nei.almoxarifadoapi.controller;

import com.ufrn.nei.almoxarifadoapi.dto.item.ItemCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.item.ItemUpdateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.mapper.RecordMapper;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordResponseDTO;
import com.ufrn.nei.almoxarifadoapi.entity.RecordEntity;
import com.ufrn.nei.almoxarifadoapi.infra.RestErrorMessage;
import com.ufrn.nei.almoxarifadoapi.infra.jwt.JwtUserDetails;
import com.ufrn.nei.almoxarifadoapi.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Operações Itens", description = "Contém as operações para gerenciar os itens e persistir com a tabela registro")
@RestController
@RequestMapping("/api/v1/operacoes")
public class OperationController {
        @Autowired
        private OperationService operationService;

        @Operation(summary = "Criar registro de consumo.",
                description = "Consumirá um item. Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "200", description = "Consumo criado com sucesso.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecordResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Erro ao consumir item. Item já consta como inativo ou item não existe.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "500", description = "Erro ao consumir item. Quantidade de itens disponiveis insuficientes.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
        })
        @PostMapping("/consumo")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RecordResponseDTO> consume(@RequestBody @Valid RecordCreateDTO createDTO) {
                RecordEntity record = operationService.toConsume(createDTO);
                RecordResponseDTO response = RecordMapper.toResponseDTO(record);

                return ResponseEntity.status(HttpStatus.OK).body(RecordMapper.toResponseDTO(record));
        }

        @Operation(summary = "Criar registro de cadastro.",
                description = "Cadastrará um novo item. Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "201", description = "Registro criado com sucesso.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecordResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Erro ao cadastrar item.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "500", description = "Erro ao cadastrar item. Objeto de criação nulo ou conflito entre códigos sipac.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
        })
        @PostMapping("/cadastro")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RecordResponseDTO> toRegister(@RequestBody @Valid ItemCreateDTO createDTO) {
                RecordEntity record = operationService.toRegister(createDTO);

                return ResponseEntity.status(HttpStatus.CREATED).body(RecordMapper.toResponseDTO(record));
        }

        @Operation(summary = "Criar registro de exclusão.",
                description = "Excluirá um item. Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
                deprecated = true,
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "200", description = "Registro excluido com sucesso.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecordResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Erro ao excluir item. Item não encontrado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "500", description = "Erro ao excluir item. Quantidade de itens disponíveis insuficientes.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                })
        @PostMapping("/exclusao")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RecordResponseDTO> toDelete(@RequestBody @Valid RecordCreateDTO createDTO) {
                RecordEntity record = operationService.toDelete(createDTO);

                return ResponseEntity.status(HttpStatus.OK).body(RecordMapper.toResponseDTO(record));
        }

        @Operation(summary = "Atualizar item.",
                description = "Atualizará as informações de um item. Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "204", description = "Item atualizado com sucesso."),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Erro ao atualizar item. Item não encontrado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "422", description = "Campos do request body foram preenchidos errados",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
                })
        @PutMapping("/correcao/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> toUpdate(@PathVariable Long id,
                                             @RequestBody @Valid ItemUpdateDTO itemUpdateDTO) {
                operationService.toUpdate(id, itemUpdateDTO);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
}
