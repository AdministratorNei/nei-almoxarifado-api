package com.ufrn.nei.almoxarifadoapi.dto.mapper;

import com.ufrn.nei.almoxarifadoapi.dto.item.ItemRecordDTO;
import com.ufrn.nei.almoxarifadoapi.dto.record.RecordResponseDTO;
import com.ufrn.nei.almoxarifadoapi.entity.RecordEntity;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class RecordMapper {
    public static RecordResponseDTO toResponseDTO(RecordEntity recordEntity) {
        ModelMapper mapper = new ModelMapper();
        RecordResponseDTO recordResponseDTO = mapper.map(recordEntity, RecordResponseDTO.class);

        recordResponseDTO.setUser(UserMapper.toRecordDTO(recordEntity.getUser()));
        recordResponseDTO.setItem(mapper.map(recordEntity.getItem(), ItemRecordDTO.class));

        return recordResponseDTO;
    }

    public static List<RecordResponseDTO> toListResponseDTO(List<RecordEntity> records) {
        return records.stream().map(i -> toResponseDTO(i)).collect(Collectors.toList());
    }
}
