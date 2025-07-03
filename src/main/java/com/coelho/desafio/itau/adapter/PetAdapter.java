package com.coelho.desafio.itau.adapter;

import com.coelho.desafio.itau.model.Pet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class PetAdapter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Pet toModel(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, Pet.class);
        } catch (Exception e) {
            System.err.println("Erro ao converter resposta da IA para Dog: " + e.getMessage());
            return null;
        }
    }

}