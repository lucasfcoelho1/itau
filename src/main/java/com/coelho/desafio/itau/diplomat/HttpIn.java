package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryDogAdapter;
import com.coelho.desafio.itau.diplomat.wire.CountryDogWireOut;
import com.coelho.desafio.itau.controller.CountryController;
import com.coelho.desafio.itau.controller.DogController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/countries")
public class HttpIn {

    private final DogController dogController;
    private final CountryController countryController;
    private final CountryDogAdapter countryDogAdapter;

    public HttpIn(DogController dogController, CountryController countryController, CountryDogAdapter countryDogAdapter) {
        this.dogController = dogController;
        this.countryController = countryController;
        this.countryDogAdapter = countryDogAdapter;
    }

    @GetMapping
    public ResponseEntity<CountryDogWireOut> getDogSuggestionByCountryName(@RequestParam String countryName) {
        var country = countryController.getCountry(countryName);
        var dogResponse = dogController.getDogAiSuggestion(country);
        var dogWireOut = countryDogAdapter.toWire(dogResponse, country);

        return ResponseEntity.ok(dogWireOut);
    }
}
