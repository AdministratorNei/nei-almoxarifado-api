package com.ufrn.nei.almoxarifadoapi.controller;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.PageableMapper;
import com.ufrn.nei.almoxarifadoapi.dto.mapper.UserMapper;
import com.ufrn.nei.almoxarifadoapi.dto.pageable.PageableDTO;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserPasswordUpdateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserResponseDTO;
import com.ufrn.nei.almoxarifadoapi.entity.UserEntity;
import com.ufrn.nei.almoxarifadoapi.infra.RestErrorMessage;
import com.ufrn.nei.almoxarifadoapi.repository.projection.UserProjection;
import com.ufrn.nei.almoxarifadoapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "Contém todas as operações relativas aos recursos para criação, edição de senha, leitura e exclusão de um usuário")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
        @Autowired
        UserService userService;

        @Operation(summary = "Cadastrar novos usuários.", description = "Cadastrará novos usuários no sistema.", responses = {
                        @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Não foi possível cadastrar o item.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @PostMapping
        public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO createDTO) {
                UserEntity user = userService.save(createDTO);

                UserResponseDTO response = UserMapper.toResponseDTO(user);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Buscar usuário pelo ID.", description = "Listará o usuário encontrado com o ID informado.", responses = {
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Usuário não foi encontrado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @GetMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN') OR ( hasRole('USER') AND #id == authentication.principal.id )")
        public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
                UserEntity user = userService.findById(id);

                UserResponseDTO response = UserMapper.toResponseDTO(user);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @Operation(summary = "Buscar todos os usuários.", description = "Listará todos os usuários.", responses = {
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class)))
        })
        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<PageableDTO> findAll(Pageable pageable) {
                Page<UserProjection> users = userService.findAllPageable(pageable);
                PageableDTO response = PageableMapper.toDto(users);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @Operation(summary = "Buscar usuários pelo email.", description = "Buscará usuários pelo email utilizarando Query Params", responses = {
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Usuário não foi encontrado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @GetMapping("/query")
        @PreAuthorize("hasRole('ADMIN') OR ( hasRole('USER') AND #email == authentication.principal.username )")
        public ResponseEntity<UserResponseDTO> findByEmail(@RequestParam String email) {
                UserEntity user = userService.findByEmail(email);

                UserResponseDTO response = UserMapper.toResponseDTO(user);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @Operation(summary = "Atualizar senha de usuário", description = "Atualizará a senha do usuário.", responses = {
                        @ApiResponse(responseCode = "200", description = "Senha atualizada com sucesso."),
                        @ApiResponse(responseCode = "400", description = "Erro na validação das senhas informadas.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @PutMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN') OR ( hasRole('USER') AND #id == authentication.principal.id )")
        public ResponseEntity<Void> updatePassword(@RequestBody @Valid UserPasswordUpdateDTO passwordUpdateDTO,
                        @PathVariable Long id) {
                userService.updatePassword(passwordUpdateDTO.getCurrentPassword(),
                                passwordUpdateDTO.getNewPassword(), passwordUpdateDTO.getConfirmPassword(), id);

                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Deletar usuários cadastrados.", description = "Excluirá o usuário pelo ID.", responses = {
                        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "O usuário especificado não foi encontrado.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
        })
        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN') OR ( hasRole('USER') AND #id == authentication.principal.id )")
        public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
                userService.deleteUser(id);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
}
