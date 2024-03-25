package com.ufrn.nei.almoxarifadoapi.dto.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import com.ufrn.nei.almoxarifadoapi.dto.item.ItemCreateDTO;
import com.ufrn.nei.almoxarifadoapi.dto.item.ItemResponseDTO;
import com.ufrn.nei.almoxarifadoapi.entity.ItemEntity;

public class ItemMapper {

    public static ItemResponseDTO toResponseDTO(ItemEntity itemEntity) {
        return new ModelMapper().map(itemEntity, ItemResponseDTO.class);
    }

    public static List<ItemResponseDTO> toListResponseDTO(List<ItemEntity> items) {
        return items.stream().map(i -> toResponseDTO(i)).collect(Collectors.toList());
    }

    public static ItemEntity toItem(ItemCreateDTO itemDTO) {
        return new ModelMapper().map(itemDTO, ItemEntity.class);
    }

    public static ItemEntity toItem(ItemResponseDTO itemDTO) {
        return new ModelMapper().map(itemDTO, ItemEntity.class);
    }

}
