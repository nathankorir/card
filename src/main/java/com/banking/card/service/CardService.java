package com.banking.card.service;

import com.banking.card.dto.CardRequestDto;
import com.banking.card.dto.CardResponseDto;
import com.banking.card.mapper.CardMapper;
import com.banking.card.model.Card;
import com.banking.card.model.CardType;
import com.banking.card.model.QCard;
import com.banking.card.repository.CardRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class CardService {
    private final CardRepository cardRepository;
    private final CardMapper cardMapper;

    public CardService(CardRepository cardRepository, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
    }

    public CardResponseDto create(CardRequestDto dto) {
//        Check if card of the same type exists for the account
        cardRepository.findByAccountIdAndTypeAndVoidedFalse(dto.getAccountId(), CardType.valueOf(dto.getType())).ifPresent(c -> {
            throw new IllegalArgumentException("This account already has a card of type " + dto.getType());
        });

//        Check if account already has 2 non-voided cards
        List<Card> existingCards = cardRepository.findByAccountIdAndVoidedFalse(dto.getAccountId());
        if (existingCards.size() >= 2) {
            throw new IllegalArgumentException("An account cannot have more than 2 active cards.");
        }

        return cardMapper.toDtoMasked(cardRepository.save(cardMapper.toEntity(dto)));
    }

    public Optional<CardResponseDto> get(UUID id) {
        return cardRepository.findById(id).map(cardMapper::toDtoMasked);
    }

    public CardResponseDto updateAlias(UUID id, String alias) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));

        card.setAlias(alias);
        Card updatedCard = cardRepository.save(card);

        return cardMapper.toDtoMasked(updatedCard);
    }

    public Page<CardResponseDto> search(
            String alias,
            CardType type,
            String pan,
            Pageable pageable,
            boolean maskFields
    ) {
        BooleanExpression predicate = buildCardPredicate(alias, type, pan);
        Page<Card> cards = cardRepository.findAll(predicate, pageable);

        return cards.map(card ->
                maskFields ? cardMapper.toDtoMasked(card) : cardMapper.toDto(card)
        );
    }

    public void delete(UUID id) {
        Card card = cardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Card does not exist"));
        card.setVoided(true);
        cardRepository.save(card);
    }

    public static BooleanExpression buildCardPredicate(String alias, CardType type, String pan) {
        QCard qCard = QCard.card;
        BooleanExpression predicate = qCard.voided.isFalse();

        if (StringUtils.hasText(alias)) {
            predicate = predicate.and(qCard.alias.eq(alias));
        }

        if (type != null) {
            predicate = predicate.and(qCard.type.eq(type));
        }

        if (StringUtils.hasText(pan)) {
            predicate = predicate.and(qCard.pan.eq(pan));
        }

        return predicate;
    }
}
