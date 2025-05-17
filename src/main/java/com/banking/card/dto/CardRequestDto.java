package com.banking.card.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDto {
    private String alias;
    private UUID accountId;
    private String type;
    private String pan;
    private String cvv;
}
