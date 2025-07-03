package com.coelho.desafio.itau.diplomat.wire.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class PetSuggestionWireOut {
    private CountryWireOut country;
    private PetWireOut pet;
}