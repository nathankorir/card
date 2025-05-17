package com.banking.card.mapper;


import com.banking.card.dto.CardRequestDto;
import com.banking.card.dto.CardResponseDto;
import com.banking.card.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardResponseDto toDto(Card card);

    Card toEntity(CardRequestDto dto);

    @Mapping(target = "pan", expression = "java(maskPan(card.getPan()))")
    @Mapping(target = "cvv", expression = "java(maskCvv(card.getCvv()))")
    CardResponseDto toDtoMasked(Card card);

    @Named("maskPan")
    default String maskPan(String pan) {
        return "****************";
    }

    @Named("maskCvv")
    default String maskCvv(String cvv) {
        return "***";
    }
}
