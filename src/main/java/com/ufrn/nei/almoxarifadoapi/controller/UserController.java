package com.ufrn.nei.almoxarifadoapi.controller;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.PageableMapper;
import com.ufrn.nei.almoxarifadoapi.dto.mapper.UserMapper;
import com.ufrn.nei.almoxarifadoapi.dto.pageable.PageableDTO;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserForgotPasswordUpdateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserPasswordUpdateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.user.UserResponseDTO;
import com.ufrn.nei.almoxarifadoapi.entity.UserEntity;
import com.ufrn.nei.almoxarifadoapi.infra.RestErrorMessage;
import com.ufrn.nei.almoxarifadoapi.repository.projection.UserProjection;
import com.ufrn.nei.almoxarifadoapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários", description = "Contém todas as operações relativas aos recursos para criação, edição de senha, leitura e exclusão de um usuário")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
        @Autowired
        UserService userService;

        @Operation(summary = "Cadastrar um usuário",
                description = "Cadastrará um novo usuário no sistema. NÃO precisa de Bearer Token.",
                responses = {
                        @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                        @ApiResponse(responseCode = "422", description = "Campo(s) inválido(s)",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
                })
        @PostMapping
        public ResponseEntity<UserResponseDTO> createUser(@RequestBody @Valid UserCreateDTO createDTO) {
                UserEntity user = userService.save(createDTO);

                UserResponseDTO response = UserMapper.toResponseDTO(user);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @Operation(summary = "Buscar um usuário pelo ID",
                description = "Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN', 'USER'.",
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "403", description = "Erro. O usuário passou o ID de outro usuário.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
                })
        @GetMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN') OR ( hasRole('USER') AND #id == authentication.principal.id )")
        public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
                UserEntity user = userService.findById(id);

                UserResponseDTO response = UserMapper.toResponseDTO(user);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @Operation(summary = "Buscar todos os usuários.",
                description = "Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
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
                        @ApiResponse(responseCode = "200", description = "Usuários encontrados com sucesso",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageableDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
                })
        @GetMapping
        @PreAuthorize("hasRole('ADMIN')")
        public ResponseEntity<PageableDTO> findAll(Pageable pageable) {
                Page<UserProjection> users = userService.findAllPageable(pageable);
                PageableDTO response = PageableMapper.toDto(users);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @Operation(summary = "Buscar todos os usuários.",
                description = "Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN'.",
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
                        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageableDTO.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "403", description = "Erro. O usuário passou o email de outro usuário.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Erro. O usuário não foi encontrado.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
                })
        @GetMapping("/query")
        @PreAuthorize("hasRole('ADMIN') OR ( hasRole('USER') AND #email == authentication.principal.username )")
        public ResponseEntity<UserResponseDTO> findByEmail(@RequestParam String email) {
                UserEntity user = userService.findByEmail(email);

                UserResponseDTO response = UserMapper.toResponseDTO(user);

                return ResponseEntity.status(HttpStatus.OK).body(response);
        }

        @Operation(summary = "Atualizar senha de usuário",
                description = "Requsição exige o uso de um bearer token.",
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Erro na validação da senha",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "403", description = "Erro. O usuário passou o ID de outro usuário.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
    })

        @PutMapping("updatePassword/{id}")
        @PreAuthorize("hasRole('ADMIN') OR ( hasRole('USER') AND #id == authentication.principal.id)")
        public ResponseEntity<Void> updatePassword(@RequestBody @Valid UserPasswordUpdateDTO passwordUpdateDTO, @PathVariable Long id) {
            userService.updatePassword(passwordUpdateDTO.getCurrentPassword(),
                        passwordUpdateDTO.getNewPassword(), passwordUpdateDTO.getConfirmPassword(), id);

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        @Operation(summary = "Atualizar senha esquecida de usuário",
                description = "Requsição exige o uso de um bearer token. Deve prover o token de recuperação de senha para ser validado.",
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))),
                        @ApiResponse(responseCode = "400", description = "Erro na validação da senha",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "403", description = "Erro. O usuário passou o ID de outro usuário.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
        })
        @PutMapping("updateForgotPassword/{email}")
        public ResponseEntity<Void> updateForgotPassword(@RequestBody @Valid UserForgotPasswordUpdateDTO passwordUpdateDTO, @PathVariable String email) {
            userService.updatePassword(email, passwordUpdateDTO.getRecoveryToken(),
                        passwordUpdateDTO.getNewPassword(), passwordUpdateDTO.getConfirmPassword());

            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        @Operation(summary = "Excluir conta de usuário",
                description = "Requsição exige o uso de um bearer token. Acesso restrito a role='ADMIN', 'USER'.",
                security = @SecurityRequirement(name = "security"),
                responses = {
                        @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso.",
                                content = @Content(mediaType = "application/json")),
                        @ApiResponse(responseCode = "401", description = "Usuário não está autenticado",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "403", description = "Erro. O usuário passou o ID de outro usuário.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class))),
                        @ApiResponse(responseCode = "404", description = "Usuário não encontrado.",
                                content = @Content(mediaType = "application/json", schema = @Schema(implementation = RestErrorMessage.class)))
                })

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('ADMIN') OR ( hasRole('USER') AND #id == authentication.principal.id )")
        public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
                userService.deleteUser(id);

                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
}
