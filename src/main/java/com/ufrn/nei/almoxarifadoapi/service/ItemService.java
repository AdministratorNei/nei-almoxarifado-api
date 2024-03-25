package com.ufrn.nei.almoxarifadoapi.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.ufrn.nei.almoxarifadoapi.exception.EntityNotFoundException;
import com.ufrn.nei.almoxarifadoapi.exception.ItemNotActiveException;
import com.ufrn.nei.almoxarifadoapi.exception.NotAvailableQuantityException;
import com.ufrn.nei.almoxarifadoapi.exception.OperationErrorException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufrn.nei.almoxarifadoapi.dto.item.ItemCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.item.ItemUpdateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.mapper.ItemMapper;
import com.ufrn.nei.almoxarifadoapi.entity.ItemEntity;
import com.ufrn.nei.almoxarifadoapi.enums.ItemQuantityOperation;
import com.ufrn.nei.almoxarifadoapi.repository.ItemRepository;

import jakarta.transaction.Transactional;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    public List<ItemEntity> findAllItems() {
        return itemRepository.findAllByAvailableTrue();
    }

    @Transactional
    public ItemEntity findById(Long id) {
        ItemEntity item = itemRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item não encontrado com id=%s", id)));

        return item;
    }

    @Transactional
    public ItemEntity findByName(String name) {
        ItemEntity item = itemRepository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(String.format("Item não encontrado com nome=%s", name)));

        return item;
    }

    @Transactional
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
        }

        item.setAvailable(true);
        itemRepository.save(item);

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
        if (data.getSipacCode() != null) {
            item.setSipacCode(data.getSipacCode());
        }
        item.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

        itemRepository.save(item);
        return item;
    }

    @Transactional
    public void deleteItem(Long id, int quantity) {
        ItemEntity item = findById(id);

        if (item.getAvailable().equals(false)) {
            throw new ItemNotActiveException();
        }

        if (quantity > item.getQuantity()) {
            throw new NotAvailableQuantityException("Não há itens disponiveis suficientes para realizar a exclusão.");
        }

        setItemQuantity(item, quantity, ItemQuantityOperation.SUBTRACT);

        itemRepository.save(item);
    }
}
