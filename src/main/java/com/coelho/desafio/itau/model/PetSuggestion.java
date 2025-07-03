package com.coelho.desafio.itau.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PetSuggestion {
    private Country country;
    private Pet pet;
}