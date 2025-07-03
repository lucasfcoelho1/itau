package com.coelho.desafio.itau.service;

import com.coelho.desafio.itau.diplomat.HttpOut;
import com.coelho.desafio.itau.logic.Logic;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.PetSuggestion;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PetService {
    private final CacheService cacheService;
    private final Logic logic;
    private final HttpOut httpOut;

    public PetService(CacheService cacheService, Logic logic, HttpOut httpOut) {
        this.cacheService = cacheService;
        this.logic = logic;
        this.httpOut = httpOut;
    }

    public PetSuggestion buildPetSuggestionFromCountry(Country country) {
        return Optional.ofNullable(country)
                .map(c -> {
                    var prompt = logic.generatePrompt(c);
                    return Optional.ofNullable(prompt)
                            .map(p -> {
                                var pet = httpOut.fetchPetSuggestionByCountryPrompt(p);
                                if (pet == null) return null;
                                var suggestion = new PetSuggestion(c, pet);
                                cacheService.set(logic.buildCacheKey(c.getTitle()), suggestion);
                                return suggestion;
                            }).orElse(null);
                }).orElse(null);
    }

    public PetSuggestion getPetSuggestionCached(String countryName) {
        return cacheService.get(logic.buildCacheKey(countryName), PetSuggestion.class);
    }
}
