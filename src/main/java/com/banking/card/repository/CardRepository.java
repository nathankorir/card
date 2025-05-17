package com.banking.card.repository;

import com.banking.card.model.Card;
import com.banking.card.model.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID>, QuerydslPredicateExecutor<Card> {
    Optional<Card> findByAccountIdAndTypeAndVoidedFalse(UUID accountId, CardType type);

    List<Card> findByAccountIdAndVoidedFalse(UUID accountId);
}