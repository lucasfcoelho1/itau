package com.coelho.desafio.itau.adapter;

import com.coelho.desafio.itau.diplomat.wire.out.PetSuggestionWireOut;
import com.coelho.desafio.itau.diplomat.wire.out.CountryWireOut;
import com.coelho.desafio.itau.diplomat.wire.out.PetWireOut;
import com.coelho.desafio.itau.model.PetSuggestion;
import org.springframework.stereotype.Service;

@Service
public class PetSuggestionAdapter {

    public PetSuggestionWireOut toWire(PetSuggestion petSuggestion) {
        var wireCountry = new CountryWireOut(
                petSuggestion.getCountry().getTitle(),
                petSuggestion.getCountry().getRegion(),
                petSuggestion.getCountry().getTotalPopulation(),
                petSuggestion.getCountry().getFlagUrl() != null ? petSuggestion.getCountry().getFlagUrl() : null
        );

        var petWireOut = new PetWireOut(
                petSuggestion.getPet().getBreed(),
                petSuggestion.getPet().getDescription()
        );

        return new PetSuggestionWireOut(wireCountry, petWireOut);
    }
}