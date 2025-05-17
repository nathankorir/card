package com.banking.card.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardResponseDto {
    private UUID id;
    private String alias;
    private UUID accountId;
    private String type;
    private String pan;
    private String cvv;
    private String voided;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
