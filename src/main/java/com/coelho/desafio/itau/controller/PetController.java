package com.coelho.desafio.itau.controller;

import com.coelho.desafio.itau.model.PetSuggestion;
import com.coelho.desafio.itau.service.PetService;
import org.springframework.stereotype.Service;

@Service
public class PetController {

    private final PetService petService;
    private final CountryController countryController;

    public PetController(PetService petService, CountryController countryController) {
        this.petService = petService;
        this.countryController = countryController;
    }

    public PetSuggestion generatePetSuggestionByCountryName(String countryName) {
        var petSuggestionCached = petService.getPetSuggestionCached(countryName);
        if (petSuggestionCached != null) return petSuggestionCached;

        var country = countryController.getCountry(countryName);

        var petSuggestion = petService.buildPetSuggestionFromCountry(country);

        return petSuggestion;
    }
}
