package com.ufrn.nei.almoxarifadoapi.service;

import com.ufrn.nei.almoxarifadoapi.dto.item.ItemCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.mapper.ItemMapper;
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

    public void toDelete(Long itemId, JwtUserDetails userDetails) {
        ItemEntity item = itemService.findById(itemId);

        RecordCreateDTO recordCreateDTO = new RecordCreateDTO(userDetails.getId(), itemId, item.getQuantity());
        RecordEntity record = recordService.save(recordCreateDTO, RecordOperationEnum.EXCLUSAO);
        itemService.setLastRecord(item, record);

        item.setQuantity(0);
        item.setAvailable(Boolean.FALSE);
        itemService.itemSave(item);
    }
}
