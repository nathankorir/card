package com.banking.card.controller;

import com.banking.card.dto.CardRequestDto;
import com.banking.card.dto.CardResponseDto;
import com.banking.card.model.CardType;
import com.banking.card.service.CardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/cards")
public class CardController {
    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    public ResponseEntity<CardResponseDto> create(@RequestBody @Valid CardRequestDto dto) {
        return ResponseEntity.ok(cardService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponseDto> get(@PathVariable UUID id) {
        return cardService.get(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Page<CardResponseDto> search(
            @RequestParam(required = false) String alias,
            @RequestParam(required = false) CardType type,
            @RequestParam(required = false) String pan,
            @RequestParam(required = false, defaultValue = "true") boolean maskFields,
            Pageable pageable
    ) {
        return cardService.search(alias, type, pan, pageable, maskFields);
    }

    @PatchMapping("/{id}/alias")
    public ResponseEntity<CardResponseDto> updateAlias(@PathVariable UUID id, @RequestParam String alias) {
        return ResponseEntity.ok(cardService.updateAlias(id, alias));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        cardService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
