package com.coelho.desafio.itau.adapter;

import com.coelho.desafio.itau.model.Dog;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class DogAdapter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Dog toModel(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, Dog.class);
        } catch (Exception e) {
            System.err.println("Erro ao converter resposta da IA para Dog: " + e.getMessage());
            return null;
        }
    }

}