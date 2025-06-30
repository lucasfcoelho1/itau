package com.coelho.desafio.itau.controller;

import com.coelho.desafio.itau.diplomat.HttpOut;
import com.coelho.desafio.itau.logic.AiSuggestionLogic;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Dog;
import org.springframework.stereotype.Service;

@Service
public class DogController {


    private final HttpOut httpOut;
    private final AiSuggestionLogic aiSuggestionLogic;

    public DogController(HttpOut httpOut, AiSuggestionLogic aiSuggestionLogic) {
        this.httpOut = httpOut;
        this.aiSuggestionLogic = aiSuggestionLogic;
    }

    public String getAiPrompt(Country country) {
        return aiSuggestionLogic.generatePrompt(country);
    }

    public Dog getDogAiSuggestion(Country country) {
        var prompt = getAiPrompt(country);
        return httpOut.fetchDogSuggestionByCountryPrompt(prompt);
    }

}
