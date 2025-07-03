package com.coelho.desafio.itau.service;

import com.coelho.desafio.itau.logic.Logic;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Pet;
import com.coelho.desafio.itau.model.PetSuggestion;
import org.springframework.stereotype.Service;

@Service
public class PetService {
    private final CacheService cacheService;
    private final Logic logic;

    public PetService(CacheService cacheService, Logic logic) {
        this.cacheService = cacheService;
        this.logic = logic;
    }

    public PetSuggestion buildPetSuggestionFromCountry(Country country) {
        var prompt = logic.generatePrompt(country);
        //var pet = httpOut.fetchDogSuggestionByCountryPrompt(prompt);
        var pet = new Pet("1", "2"); //httpOut.fetchDogSuggestionByCountryPrompt(prompt);

        var petSuggestion = new PetSuggestion(country, pet);

        cacheService.set(logic.buildCacheKey(country.getTitle()), petSuggestion);
        return petSuggestion;
    }

    public PetSuggestion getPetSuggestionCached(String countryName) {
        return cacheService.get(logic.buildCacheKey(countryName), PetSuggestion.class);
    }
}
