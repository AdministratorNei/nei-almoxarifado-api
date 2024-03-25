package com.ufrn.nei.almoxarifadoapi.controller;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.RecordMapper;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordCreateItemDTO;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordResponseDTO;
import com.ufrn.nei.almoxarifadoapi.entity.RecordEntity;
import com.ufrn.nei.almoxarifadoapi.infra.RestErrorMessage;
import com.ufrn.nei.almoxarifadoapi.service.OperationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Operations", description = "Contém as operações para gerenciar os itens e persistir com a tabela registro")
@RestController
@RequestMapping("/api/v1/operacoes")
public class OperationController {
        @Autowired
        private OperationService operationService;

        @Operation(summary = "Criar registro de consumo.", description = "Criará um novo registro de consumo no sistema.", responses = {
                        @ApiResponse(responseCode = "200", description = "Registro criado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecordResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Não foi possível criar o registro.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @PostMapping("/consumo")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RecordResponseDTO> consume(@RequestBody @Valid RecordCreateDTO createDTO) {
                RecordEntity record = operationService.toConsume(createDTO);

                return ResponseEntity.status(HttpStatus.OK).body(RecordMapper.toResponseDTO(record));
        }

        @Operation(summary = "Adicionar itens.", description = "Realizará o cadastro de novos itens ao sistema.", responses = {
                        @ApiResponse(responseCode = "200", description = "Cadastro realizada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecordResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Não foi possível realizar o cadastro.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @PostMapping("/cadastro")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RecordResponseDTO> toRegister(@RequestBody @Valid RecordCreateItemDTO createDTO) {
                RecordEntity record = operationService.toRegister(createDTO);

                return ResponseEntity.status(HttpStatus.OK).body(RecordMapper.toResponseDTO(record));
        }

        @Operation(summary = "Excluir itens.", description = "Deletará o item desejado do sistema.", responses = {
                        @ApiResponse(responseCode = "200", description = "Deleção realizada com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecordResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Não foi possível deletar o item.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @PostMapping("/exclusao")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RecordResponseDTO> toDelete(@RequestBody @Valid RecordCreateDTO createDTO) {
                RecordEntity record = operationService.toDelete(createDTO);

                return ResponseEntity.status(HttpStatus.OK).body(RecordMapper.toResponseDTO(record));
        }
}
