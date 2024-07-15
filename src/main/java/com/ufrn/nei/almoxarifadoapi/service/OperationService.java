package com.ufrn.nei.almoxarifadoapi.service;

import com.ufrn.nei.almoxarifadoapi.dto.item.ItemCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.item.ItemUpdateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordCreateDTO;
import com.ufrn.nei.almoxarifadoapi.entity.ItemEntity;
import com.ufrn.nei.almoxarifadoapi.entity.RecordEntity;
import com.ufrn.nei.almoxarifadoapi.enums.RecordOperationEnum;
import com.ufrn.nei.almoxarifadoapi.infra.jwt.JwtAuthenticationContext;
import com.ufrn.nei.almoxarifadoapi.infra.jwt.JwtUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OperationService {
    @Autowired
    private ItemService itemService;

    @Autowired
    private RecordService recordService;

    public RecordEntity toConsume(RecordCreateDTO createDTO) {
        itemService.deleteOrConsumeItem(createDTO.getItemID(), createDTO.getQuantity());

        RecordEntity record = recordService.save(createDTO, RecordOperationEnum.CONSUMO);

        itemService.setLastRecord(itemService.findById(createDTO.getItemID()), record);

        return record;
    }

    public RecordEntity toRegister(ItemCreateDTO createDTO) {
        ItemEntity item = itemService.createItem(createDTO);
        RecordCreateDTO recordCreateDTO =
                new RecordCreateDTO(JwtAuthenticationContext.getId(), item.getId(), createDTO.getQuantity());
        RecordEntity record = recordService.save(recordCreateDTO, RecordOperationEnum.CADASTRO);

        itemService.setLastRecord(item, record);

        return record;
    }

    public RecordEntity toDelete(RecordCreateDTO createDTO) {
        itemService.deleteOrConsumeItem(createDTO.getItemID(), createDTO.getQuantity());

        RecordEntity record = recordService.save(createDTO, RecordOperationEnum.EXCLUSAO);

        itemService.setLastRecord(itemService.findById(createDTO.getItemID()), record);

        return record;
    }

    public void toUpdate(Long id, ItemUpdateDTO itemUpdateDTO) {
        itemService.updateItem(id, itemUpdateDTO);
    }
}
