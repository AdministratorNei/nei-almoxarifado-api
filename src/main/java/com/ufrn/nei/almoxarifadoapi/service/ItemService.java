package com.ufrn.nei.almoxarifadoapi.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.ufrn.nei.almoxarifadoapi.dto.item.ItemUpdateIdealAmountDTO;
import com.ufrn.nei.almoxarifadoapi.entity.RecordEntity;
import com.ufrn.nei.almoxarifadoapi.entity.RoleEntity;
import com.ufrn.nei.almoxarifadoapi.entity.UserEntity;
import com.ufrn.nei.almoxarifadoapi.exception.EntityNotFoundException;
import com.ufrn.nei.almoxarifadoapi.exception.ItemNotActiveException;
import com.ufrn.nei.almoxarifadoapi.exception.NotAvailableQuantityException;
import com.ufrn.nei.almoxarifadoapi.exception.OperationErrorException;

import com.ufrn.nei.almoxarifadoapi.infra.jwt.JwtAuthenticationContext;
import com.ufrn.nei.almoxarifadoapi.infra.mail.MailService;
import com.ufrn.nei.almoxarifadoapi.repository.projection.ItemProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ufrn.nei.almoxarifadoapi.dto.item.ItemCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.item.ItemUpdateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.mapper.ItemMapper;
import com.ufrn.nei.almoxarifadoapi.entity.ItemEntity;
import com.ufrn.nei.almoxarifadoapi.enums.ItemQuantityOperation;
import com.ufrn.nei.almoxarifadoapi.repository.ItemRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private RoleService roleService;

    private final String ROLE_ADMIN = "ROLE_ADMIN";

    @Transactional(readOnly = true)
    public Page<ItemProjection> findAllItems(Pageable pageable) {
        return itemRepository.findAllByAvailableTrue(pageable);
    }

    @Transactional(readOnly = true)
    public ItemEntity findById(Long id) {
        ItemEntity item = itemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item não encontrado com id=%s", id)));

        return item;
    }

    @Transactional(readOnly = true)
    public ItemEntity findByName(String name) {
        ItemEntity item = itemRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item não encontrado com nome=%s", name)));

        return item;
    }

    @Transactional(readOnly = true)
    public ItemEntity findByCode(Long sipacCode) {
        ItemEntity item = itemRepository.findBySipacCode(sipacCode).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item não encontrado com código=%s", sipacCode)));

        return item;
    }

    @Transactional
    public ItemEntity createItem(ItemCreateDTO data) {
        if (data == null) {
            throw new OperationErrorException("Dados para criação de item não podem ser nulos!");
        }

        // Para ter um valor padrão.
        ItemEntity item = ItemMapper.toItem(data);

        // Busca por um item previamente cadastrado para, ao invés de cadastrar
        // novamente, atualizar quantidade.
        try {
            if (data.getSipacCode() != null) {
                item = findByCode(data.getSipacCode());

                if (data.getName().equalsIgnoreCase(item.getName())) {
                    setItemQuantity(item, data.getQuantity(), ItemQuantityOperation.SUM);
                } else {
                    throw new OperationErrorException("Encontrado outro item com nome diferente e mesmo código SIPAC.");
                }
            } else {
                item = findByName(data.getName());
                setItemQuantity(item, data.getQuantity(), ItemQuantityOperation.SUM);
            }

            item.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        } catch (EntityNotFoundException ex) {
            UserEntity user = userService.findById(JwtAuthenticationContext.getId());
            item.setCreatedBy(user);
        }

        item.setAvailable(true);
        itemRepository.save(item);

        return item;
    }

    @Transactional
    public ItemEntity setLastRecord(ItemEntity item, RecordEntity record) {
        item.setLastRecord(record);
        item = itemRepository.save(item);

        return item;
    }

    private void setItemQuantity(ItemEntity item, int quantity, ItemQuantityOperation operation) {
        if (quantity <= 0 || item == null) {
            throw new OperationErrorException();
        }

        switch (operation) {
            case SUM:
                item.setQuantity(quantity + item.getQuantity());
                item.setAvailable(true);
                break;
            case SUBTRACT:
                if (quantity > item.getQuantity()) {
                    throw new OperationErrorException("Não há itens disponiveis suficientes para realizar a exclusão.");
                } else {
                    item.setQuantity(item.getQuantity() - quantity);
                }

                if (item.getQuantity() == 0) {
                    item.setAvailable(false);
                }

                break;
            default:
                throw new OperationErrorException("Não foi definida uma operação.");
        }
    }

    @Transactional
    public ItemEntity updateItem(Long id, ItemUpdateDTO data) {
        ItemEntity item = findById(id);

        if (data.getName() != null && !data.getName().isBlank()) {
            item.setName(data.getName());
        }
        if (data.getType() != null && !data.getType().isBlank()) {
            item.setType(data.getType());
        }
        if (data.getSipacCode() != null) {
            item.setSipacCode(data.getSipacCode());
        }
        if (data.getMinimumStockLevel() != null) {
            item.setMinimumStockLevel(data.getMinimumStockLevel());
        }
        if (data.getQuantity() != null) {
            item.setQuantity(data.getQuantity());
        }

        item.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
        itemRepository.save(item);

        return item;
    }

    @Transactional
    public void deleteOrConsumeItem(Long id, int quantity) {
        ItemEntity item = findById(id);

        if (item.getAvailable().equals(false)) {
            throw new ItemNotActiveException();
        }

        if (quantity > item.getQuantity()) {
            throw new NotAvailableQuantityException("Quantidade de itens disponiveis insuficientes.");
        }

        // Os e-mails são enviados quando a quantidade do item é menor que a quantidade ideal. Entretanto,
        // o e-email só será enviado uma vez, ou seja, se o item já estava com a quantidade atual menor
        // que a quantidade ideal, o e-mail não será enviado novamente.
        // O e-mail só será enviado se após a diminuição do item pelo método 'setItemQuantity()' satisfazer a condição
        // descrita acima.

        int quantityBeforeUpdate = item.getQuantity();
        Boolean sendMail = quantityBeforeUpdate <= item.getMinimumStockLevel() ? Boolean.FALSE : Boolean.TRUE;

        setItemQuantity(item, quantity, ItemQuantityOperation.SUBTRACT);

        sendMailLowStock(item, sendMail);

        itemRepository.save(item);
    }

    @Transactional
    public void updateIdealAmount(Long id, ItemUpdateIdealAmountDTO dto) {
        ItemEntity item = findById(id);
        item.setMinimumStockLevel(dto.getIdealAmount());

        sendMailLowStock(item, Boolean.TRUE);

        itemRepository.save(item);
    }

    private void sendMailLowStock(ItemEntity item, Boolean sendMail) {
        if (item.getQuantity() <= item.getMinimumStockLevel() && sendMail) {
            RoleEntity roleAdmin = roleService.findByRoleName(ROLE_ADMIN).get();
            List<UserEntity> users = userService.findAllByRole(roleAdmin);

            String[] usersEmail = users.stream()
                    .map(UserEntity::getEmail) // Obtém o email de cada UserEntity
                    .toArray(String[]::new);

            mailService.sendMailLowStock(usersEmail, item.getName(), item.getQuantity(), item.getMinimumStockLevel());
        }
    }
}
