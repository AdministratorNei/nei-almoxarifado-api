package com.ufrn.nei.almoxarifadoapi.service;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.RequestMapper;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.request.RequestAdminCommentDTO;
import com.ufrn.nei.almoxarifadoapi.dto.request.RequestCreateDTO;
import com.ufrn.nei.almoxarifadoapi.entity.ItemEntity;
import com.ufrn.nei.almoxarifadoapi.entity.RequestEntity;
import com.ufrn.nei.almoxarifadoapi.entity.UserEntity;
import com.ufrn.nei.almoxarifadoapi.enums.RequestStatusEnum;
import com.ufrn.nei.almoxarifadoapi.exception.EntityNotFoundException;
import com.ufrn.nei.almoxarifadoapi.exception.ModifyStatusException;
import com.ufrn.nei.almoxarifadoapi.exception.StatusNotFoundException;
import com.ufrn.nei.almoxarifadoapi.exception.UnauthorizedAccessException;
import com.ufrn.nei.almoxarifadoapi.infra.jwt.JwtAuthenticationContext;
import com.ufrn.nei.almoxarifadoapi.infra.jwt.JwtUserDetails;
import com.ufrn.nei.almoxarifadoapi.infra.mail.MailService;
import com.ufrn.nei.almoxarifadoapi.repository.RequestRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import com.ufrn.nei.almoxarifadoapi.repository.projection.RequestProjection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RequestService {
    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OperationService operationService;

    @Autowired
    private MailService mailService;

    private final String ROLE_ADMIN = "ROLE_ADMIN";

    @Transactional
    public RequestEntity create(RequestCreateDTO data) {
        UserEntity user = userService.findById(JwtAuthenticationContext.getId());
        ItemEntity item = itemService.findById(data.getItemID());
        RequestStatusEnum status = RequestStatusEnum.PENDENTE;

        RequestEntity request = RequestMapper.toRequest(data, user, item, status);

        requestRepository.save(request);
        mailService.sendMailRequestCreatedAsync(user.getEmail(), user.getName(),
                item.getName(), request.getCreatedAt(), request.getQuantity());

        return request;
    }

    @Transactional
    public Boolean accept(Long id, RequestAdminCommentDTO adminCommentDTO) {
        RequestEntity request = findById(id);

        validateRequestStatus(request, RequestStatusEnum.ACEITO);

        RecordCreateDTO requestDTO =
                new RecordCreateDTO(request.getUser().getId(), request.getItem().getId(), Math.toIntExact(request.getQuantity()));

        if (operationService.toConsume(requestDTO) == null) {
            return Boolean.FALSE;
        }

        String comment = adminCommentDTO != null ? adminCommentDTO.getComment() : null;

        if (updateRequest(request, RequestStatusEnum.ACEITO, comment)) {
            UserEntity user = request.getUser();
            ItemEntity item = request.getItem();
            mailService.sendMailRequestAcceptedAsync(user.getEmail(), user.getName(),
                    item.getName(), request.getUpdatedAt(), request.getQuantity());

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Transactional
    public Boolean decline(Long id, RequestAdminCommentDTO adminCommentDTO) {
        RequestEntity request = findById(id);

        String comment = adminCommentDTO != null ? adminCommentDTO.getComment() : null;

        if (updateRequest(request, RequestStatusEnum.RECUSADO, comment)) {
            UserEntity user = request.getUser();
            ItemEntity item = request.getItem();
            mailService.sendMailRequestDeniedAsync(user.getEmail(), user.getName(),
                    item.getName(), request.getUpdatedAt(), request.getQuantity());

            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    @Transactional
    public Boolean cancel(Long id) {
        RequestEntity request = findById(id);

        if (updateRequest(request, RequestStatusEnum.CANCELADO, null)) {
            UserEntity user = request.getUser();
            ItemEntity item = request.getItem();
            mailService.sendMailRequestCanceledAsync(user.getEmail(), user.getName(),
                    item.getName(), request.getUpdatedAt(), request.getQuantity());

            return Boolean.TRUE;
        }

    return Boolean.FALSE;
  }

    @Transactional(readOnly = true)
    public Page<RequestProjection> findAll(JwtUserDetails userDetails, Pageable pageable) {
        Page<RequestProjection> requests;

        if (userDetails.getRole().equalsIgnoreCase(ROLE_ADMIN)) {
            requests = requestRepository.findAllPageable(pageable);
        } else {
            requests = requestRepository.findAllPageable(userDetails.getId(), pageable);
        }

        return requests;
    }

    @Transactional(readOnly = true)
    public RequestEntity findById(Long id) {
        RequestEntity request = requestRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Solicitação não encontrado com id='%s'", id)));

        // Id do usuário que fez a chamada ao método
        Long userIdRequest = JwtAuthenticationContext.getId();

        // Verificando se o usuário que chamou o controller é o dono da solicitação.
        // A exceção de acesso não autorizado não se aplica ao ADMIN,
        // por isso verificamos se quem invocou o método não possui a ROLE=ADMIN
        if (!Objects.equals(userIdRequest, request.getUser().getId()) &&
                !JwtAuthenticationContext.getAuthoritie().toString().contains("ROLE_ADMIN")) {
            throw new UnauthorizedAccessException(String.format("O usuário='%s' está tentando obter a " +
                    "solicitação de outro usuário", JwtAuthenticationContext.getEmail()));
        }

        return request;
    }

    @Transactional(readOnly = true)
    public Page<RequestProjection> findByStatus(JwtUserDetails userDetails, String status, Pageable pageable) {
        Page<RequestProjection> requests;
        // Convertendo a string de status para o enum statusEnum
        RequestStatusEnum statusEnum = Arrays.stream(RequestStatusEnum.values())
                .filter(e -> e.name().equalsIgnoreCase(status))
                .findFirst()
                .orElseThrow(() -> new StatusNotFoundException(String.format("Status='%s' não encontrado", status)));

        if (userDetails.getRole().equalsIgnoreCase(ROLE_ADMIN)) {
            requests = requestRepository.findByStatus(statusEnum, pageable);
        } else {
            requests = requestRepository.findByStatus(statusEnum, userDetails.getId(), pageable);
        }

        return requests;
    }

    @Transactional(readOnly = true)
    public Page<RequestProjection> findByUserID(Integer userId, JwtUserDetails userDetails, Pageable pageable) {
        Page<RequestProjection> requests;
        String userRole = userDetails.getRole();

        if (userRole.equalsIgnoreCase(ROLE_ADMIN)) {
            Long userIdLong = userId == 0 ? userDetails.getId() : userId.longValue();

            requests = requestRepository.findByUserId(userIdLong, pageable);
        } else {
            requests = requestRepository.findByUserId(userDetails.getId(), pageable);
        }

        return requests;
    }

    @Transactional(readOnly = true)
    public Page<RequestProjection> findByItemID(Long id, JwtUserDetails userDetails, Pageable pageable) {
        Page<RequestProjection> requests;

        if (userDetails.getRole().equalsIgnoreCase(ROLE_ADMIN)) {
            requests = requestRepository.findByItemId(id, pageable);
        } else {
            requests = requestRepository.findByItemId(id, userDetails.getId(), pageable);
        }

        return requests;
    }

    // Métodos Auxiliares

    @Transactional
    private Boolean updateRequest(RequestEntity request, RequestStatusEnum status, String adminComment) {
        validateRequestStatus(request, status);

        try {
            // Atualizar o status da solicitação
            request.setStatus(status);
            request.setAdminComment(adminComment);
            request.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
            requestRepository.save(request);
        } catch (RuntimeException err) {
            log.error(err.getMessage());
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private void validateRequestStatus(RequestEntity request, RequestStatusEnum status) {
        // Verificar se a solicitação já possui o novo status
        if (request.getStatus().equals(status)) {
            log.info("Solicitação já foi " +  status.toString().toLowerCase() +  " anteriormente");
            throw new ModifyStatusException("Solicitação já foi" +  status.toString().toLowerCase() +  "anteriormente");
        }

        // Verificar se a solicitação está pendente
        if (!request.getStatus().equals(RequestStatusEnum.PENDENTE)) {
            log.warn("Tentando alterar o status de uma solicitação que não está como pendente.");
            throw new ModifyStatusException("Não é possível alterar o status de uma solicitação que não está pendente");
        }
    }
}
