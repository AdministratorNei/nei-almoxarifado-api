package com.ufrn.nei.almoxarifadoapi.controller;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.RecordMapper;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordResponseDTO;
import com.ufrn.nei.almoxarifadoapi.entity.RecordEntity;
import com.ufrn.nei.almoxarifadoapi.infra.RestErrorMessage;
import com.ufrn.nei.almoxarifadoapi.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Records", description = "Contém todas as operações relativas aos recursos para criação e leitura dos registros")
@RestController
@RequestMapping("/api/v1/records")
public class RecordController {
        @Autowired
        private RecordService recordService;

        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<RecordResponseDTO>> findAll() {
                List<RecordEntity> recordEntityList = recordService.findAll();

                return ResponseEntity.status(HttpStatus.OK).body(RecordMapper.toListResponseDTO(recordEntityList));
        }

        @Operation(summary = "Listar registros pelo ID.", description = "Listará os registros com o ID informado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecordResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Não foi possível encontrar o registro.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<RecordResponseDTO> findById(@PathVariable Long id) {
                RecordEntity response = recordService.findById(id);

                return ResponseEntity.status(HttpStatus.OK).body(RecordMapper.toResponseDTO(response));
        }

        @Operation(summary = "Listar registros por informações do usuário.", description = "Listará os registros utilizando as informações dos usuários.", responses = {
                        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecordResponseDTO.class)))
        })
        @GetMapping("/query/users")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<RecordResponseDTO>> findByUsers(@RequestParam(required = false) Long id,
                        @RequestParam(required = false) String name,
                        @RequestParam(required = false) String email,
                        @RequestParam(required = false) String role) {
                List<RecordEntity> response = recordService.findByUsers(id, name, email, role);

                return ResponseEntity.status(HttpStatus.OK).body(RecordMapper.toListResponseDTO(response));
        }

        @Operation(summary = "Listar registros por informações do item.", description = "Listará os registros utilizando as informações dos itens.", responses = {
                        @ApiResponse(responseCode = "200", description = "Registro encontrado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RecordResponseDTO.class)))
        })
        @GetMapping("/query/itens")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<List<RecordResponseDTO>> findByItens(@RequestParam(required = false) Long id,
                        @RequestParam(required = false) Long itemTagging,
                        @RequestParam(required = false) String name) {
                List<RecordEntity> response = recordService.findByItens(id, itemTagging, name);

                return ResponseEntity.status(HttpStatus.OK).body(RecordMapper.toListResponseDTO(response));
        }
}
