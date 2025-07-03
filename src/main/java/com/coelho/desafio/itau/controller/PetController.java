package com.coelho.desafio.itau.controller;

import com.coelho.desafio.itau.model.PetSuggestion;
import com.coelho.desafio.itau.service.PetService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PetController {

    private final PetService petService;
    private final CountryController countryController;

    public PetController(PetService petService, CountryController countryController) {
        this.petService = petService;
        this.countryController = countryController;
    }

    public PetSuggestion generatePetSuggestionByCountryName(String countryName) {
        return Optional.ofNullable(petService.getPetSuggestionCached(countryName))
                .orElseGet(() -> {
                    var country = countryController.getCountry(countryName);
                    return petService.buildPetSuggestionFromCountry(country);
                });
    }
}
