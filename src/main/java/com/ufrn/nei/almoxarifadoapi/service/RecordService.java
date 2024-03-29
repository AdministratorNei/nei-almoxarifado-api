package com.ufrn.nei.almoxarifadoapi.service;

import com.ufrn.nei.almoxarifadoapi.dto.mapper.ItemMapper;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordCreateItemDTO;
import com.ufrn.nei.almoxarifadoapi.entity.ItemEntity;
import com.ufrn.nei.almoxarifadoapi.entity.RecordEntity;
import com.ufrn.nei.almoxarifadoapi.entity.UserEntity;
import com.ufrn.nei.almoxarifadoapi.enums.RecordOperationEnum;
import com.ufrn.nei.almoxarifadoapi.exception.EntityNotFoundException;
import com.ufrn.nei.almoxarifadoapi.repository.RecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class RecordService {
    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;
    
    @Transactional
    public RecordEntity save(RecordCreateDTO recordCreateDTO, RecordOperationEnum operationEnum) {
        UserEntity user = userService.findById(recordCreateDTO.getUserID());
        ItemEntity item = ItemMapper.toItem(itemService.findById(recordCreateDTO.getItemID()));
        RecordEntity record = new RecordEntity(user, item, recordCreateDTO.getQuantity(), operationEnum);

        return recordRepository.save(record);
    }

    @Transactional
    public RecordEntity save(RecordCreateItemDTO createItemDTO, ItemEntity item, RecordOperationEnum operationEnum) {
        UserEntity user = userService.findById(createItemDTO.getUserID());
        RecordEntity record = new RecordEntity(user, item, createItemDTO.getItem().getQuantityAvailable(), operationEnum);

        return recordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<RecordEntity> findAll() {
        return recordRepository.findAll();
    }

    @Transactional(readOnly = true)
    public RecordEntity findById(Long id) {
        return recordRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuário não encontrado com o id='%s'", id))
        );
    }

    @Transactional(readOnly = true)
    public List<RecordEntity> findByUsers(Long id, String name, String email, String role) {
        return recordRepository.findByUsers(id, name, email, role);
    }

    @Transactional(readOnly = true)
    public List<RecordEntity> findByItens(Long id, Long itemTagging, String name) {
        return recordRepository.findByItens(id, itemTagging, name);
    }
}
