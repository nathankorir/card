package com.banking.card.controller;

import com.banking.card.dto.CardRequestDto;
import com.banking.card.dto.CardResponseDto;
import com.banking.card.model.CardType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CardControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenCreateCardThenSuccess() throws Exception {
        CardRequestDto request = new CardRequestDto("Test Virtual Card", UUID.randomUUID(), CardType.PHYSICAL.toString(), "1234567812345678", "234");

        mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(request.getAccountId().toString()));
    }

    @Test
    void whenGetCardByIdThenReturnCard() throws Exception {
        CardRequestDto request = new CardRequestDto("Test Virtual Card", UUID.randomUUID(), CardType.PHYSICAL.toString(), "1234567812345679", "123");

        String response = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CardResponseDto created = objectMapper.readValue(response, CardResponseDto.class);
        UUID cardId = created.getId();

        mockMvc.perform(get("/cards" + "/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.accountId").value(request.getAccountId().toString()));
    }

    @Test
    void whenUpdateCardThenSuccess() throws Exception {
        CardRequestDto request = new CardRequestDto("Test Virtual Card", UUID.randomUUID(), CardType.PHYSICAL.toString(), "1234567812345779", "124");

        String response = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CardResponseDto created = objectMapper.readValue(response, CardResponseDto.class);
        UUID cardId = created.getId();
        mockMvc.perform(patch("/cards/{id}/alias", cardId)
                        .param("alias", "Test Virtual Card 2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.alias").value("Test Virtual Card 2"));
    }

    @Test
    void whenListCardsThenSuccess() throws Exception {
        mockMvc.perform(get("/cards")
                        .param("cardAlias", "Test Virtual Card")
                        .param("type", "VIRTUAL")
                        .param("pan", "1234567812345679")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    void whenSoftDeleteCardThenSuccess() throws Exception {
        CardRequestDto request = new CardRequestDto("Test Virtual Card", UUID.randomUUID(), CardType.PHYSICAL.toString(), "1234567912345679", "123");

        String response = mockMvc.perform(post("/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CardResponseDto created = objectMapper.readValue(response, CardResponseDto.class);
        UUID cardId = created.getId();

        mockMvc.perform(delete("/cards" + "/" + cardId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/cards" + "/" + cardId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardId.toString()))
                .andExpect(jsonPath("$.voided").value("true"));
    }
}
