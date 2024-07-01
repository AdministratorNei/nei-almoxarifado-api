package com.ufrn.nei.almoxarifadoapi.controller;

import com.ufrn.nei.almoxarifadoapi.dto.item.*;
import com.ufrn.nei.almoxarifadoapi.dto.mapper.PageableMapper;
import com.ufrn.nei.almoxarifadoapi.dto.pageable.PageableDTO;
import com.ufrn.nei.almoxarifadoapi.infra.RestErrorMessage;
import com.ufrn.nei.almoxarifadoapi.repository.projection.ItemProjection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.ItemMapper;
import com.ufrn.nei.almoxarifadoapi.entity.ItemEntity;
import com.ufrn.nei.almoxarifadoapi.service.ItemService;

import jakarta.validation.Valid;

@Tag(name = "Itens", description = "Contém todas as operações relativas aos recursos para criação, edição, leitura e exclusão de um item")
@RestController
@RequestMapping("api/v1/itens")
public class ItemController {
        @Autowired
        private ItemService itemService;

        @Operation(summary = "Buscar todos os itens.",
                description = "Buscará todos os itens cadastrados. Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN', 'USER'.",
                security = @SecurityRequirement(name = "security"),
                parameters = {
                        @Parameter(in = ParameterIn.QUERY, name = "page",
                                content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                                description = "Representa a página retornada."
                        ),
                        @Parameter(in = ParameterIn.QUERY, name = "size",
                                content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                                description = "Representa o total de elementos por página."
                        ),
                        @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                                array = @ArraySchema(schema = @Schema(type = "string", defaultValue = "id,asc")),
                                description = "Representa a ordenação dos resultados. Aceita múltiplos critérios de ordenação."
                        ),
                },
                responses = {
                        @ApiResponse(responseCode = "200", description = "Itens encontrados com sucesso",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageableDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @GetMapping
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public ResponseEntity<PageableDTO> getAllItems(Pageable pageable) {
                Page<ItemProjection> data = itemService.findAllItems(pageable);
                PageableDTO page = PageableMapper.toDto(data);

                return ResponseEntity.status(HttpStatus.OK).body(page);
        }

        @Operation(summary = "Buscar um item",
                description = "Buscará um item pelo seu ID. Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN', 'USER'.",
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "200", description = "Itens encontrados com sucesso.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Itens não encontrados.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
        public ResponseEntity<ItemResponseDTO> getItem(@PathVariable Long id) {
                ItemEntity data = itemService.findById(id);

                ItemResponseDTO item = ItemMapper.toResponseDTO(data);

                return ResponseEntity.status(HttpStatus.OK).body(item);
        }

        @Operation(summary = "Cadastrar um item",
                description = "Cadastrará um item. Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
                deprecated = true,
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "201", description = "Itens cadastrado com sucesso.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "500", description = "Erro ao cadastrar item.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @PostMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ItemResponseDTO> createItem(@RequestBody @Valid ItemCreateDTO itemDTO) {
                ItemEntity data = itemService.createItem(itemDTO);

                ItemResponseDTO item = ItemMapper.toResponseDTO(data);

                return ResponseEntity.status(HttpStatus.CREATED).body(item);
        }

        @Operation(summary = "Atualizar itens cadastrados.",
                description = "Atualizará as informações de um item. Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
                deprecated = true,
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<ItemResponseDTO> updateItem(@PathVariable Long id,
                        @RequestBody @Valid ItemUpdateDTO itemDTO) {
                ItemEntity data = itemService.updateItem(id, itemDTO);

                ItemResponseDTO item = ItemMapper.toResponseDTO(data);

                return ResponseEntity.status(HttpStatus.OK).body(item);
        }

        @Operation(summary = "Deletará um item.",
                description = "Deletará um item. Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
                deprecated = true,
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "204", description = "Item deletado com sucesso.",
                                content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Erro ao remover item. Item já consta como inativo ou item não existe.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "500", description = "Erro ao remover item. Quantidade de itens disponiveis insuficientes.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> deleteItem(@PathVariable Long id, @RequestBody @Valid ItemDeleteDTO deleteDTO) {
                itemService.deleteOrConsumeItem(id, deleteDTO.getQuantity());

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        @Operation(summary = "Atualizará a quantidade ideal de um item.",
                description = "Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "204", description = "Item atualizado com sucesso.",
                                content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Erro ao remover item. Item já consta como inativo ou item não existe.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
                })
        @PatchMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<Void> updateIdealAmount(@PathVariable Long id,
                                                      @RequestBody @Valid ItemUpdateIdealAmountDTO updateDTO) {
                itemService.updateIdealAmount(id, updateDTO);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
}
