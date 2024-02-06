package com.ufrn.nei.almoxarifadoapi.controller;

import java.util.List;

import com.ufrn.nei.almoxarifadoapi.exception.CreateEntityException;
import com.ufrn.nei.almoxarifadoapi.exception.EntityNotFoundException;
import com.ufrn.nei.almoxarifadoapi.infra.RestErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ufrn.nei.almoxarifadoapi.dto.item.ItemCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.item.ItemResponseDTO;
import com.ufrn.nei.almoxarifadoapi.dto.item.ItemUpdateDTO;
import com.ufrn.nei.almoxarifadoapi.service.ItemService;

import jakarta.validation.Valid;

@Tag(name = "Itens",
    description = "Contém todas as operações relativas aos recursos para criação, edição, leitura e exclusão de um item"
)
@RestController
@RequestMapping("api/v1/itens")
public class ItemController {
    @Autowired
    private ItemService itemService;

    @Operation(summary = "Buscar todos os itens", description = "Listará todos os itens cadastrados",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Itens encontrados com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponseDTO.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getAllItems() {
        List<ItemResponseDTO> items = itemService.findAllItems();

        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @Operation(summary = "Buscar itens pelo ID.", description = "Listará o item encontrado com o ID informado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item encontrado com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Item não foi encontrado.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItem(@PathVariable Long id) {
        ItemResponseDTO item = itemService.findItem(id);

        return ResponseEntity.status(HttpStatus.OK).body(item);
    }

    @Operation(summary = "Cadastrar novos itens.", description = "Cadastrará novos itens no sistema.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Item cadastrado com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Não foi possível cadastrar o item.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            }
    )
    @PostMapping
    public ResponseEntity<ItemResponseDTO> createItem(@RequestBody ItemCreateDTO itemDTO) {
        ItemResponseDTO item = itemService.createItem(itemDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @Operation(summary = "Atualizar itens cadastrados.", description = "Atualizará as informações do item específicado.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Item atualizado com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "O item especificado não foi encontrado.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> updateItem(@PathVariable Long id, @RequestBody @Valid ItemUpdateDTO itemDTO) {
        ItemResponseDTO item = itemService.updateItem(id, itemDTO);

        return ResponseEntity.status(HttpStatus.OK).body(item);
    }

    @Operation(summary = "Deletar itens cadastrados.", description = "Excluirá o item especificado.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Item deletado com sucesso.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ItemResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "O item especificado não foi encontrado.",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        boolean result = itemService.deleteItem(id);
        return result == true
                ? ResponseEntity.status(HttpStatus.NO_CONTENT).build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}
