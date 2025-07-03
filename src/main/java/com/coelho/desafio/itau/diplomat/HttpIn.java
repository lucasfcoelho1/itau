package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryDogAdapter;
import com.coelho.desafio.itau.controller.CountryController;
import com.coelho.desafio.itau.controller.DogController;
import com.coelho.desafio.itau.diplomat.wire.out.CountryDogWireOut;
import com.coelho.desafio.itau.exception.ExternalServiceException;
import com.coelho.desafio.itau.service.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpIn {

    private final DogController dogController;
    private final CountryController countryController;
    private final CountryDogAdapter countryDogAdapter;
    private final CacheService cacheService;

    public HttpIn(
            DogController dogController,
            CountryController countryController,
            CountryDogAdapter countryDogAdapter,
            CacheService cacheService
    ) {
        this.dogController = dogController;
        this.countryController = countryController;
        this.countryDogAdapter = countryDogAdapter;
        this.cacheService = cacheService;
    }

    @GetMapping("/api/countries/{countryName}/dogs")
    public ResponseEntity<CountryDogWireOut> getDogSuggestionByCountryName(@PathVariable String countryName) {
        String cacheKey = "countryDog:" + countryName.toLowerCase();

        return getFromCacheOrGenerate(cacheKey, countryName);
    }

    private ResponseEntity<CountryDogWireOut> getFromCacheOrGenerate(String cacheKey, String countryName) {
        CountryDogWireOut cached = cacheService.get(cacheKey, CountryDogWireOut.class);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        var country = countryController.getCountry(countryName);
        var dog = dogController.getDogAiSuggestion(country);

        if (country == null || dog == null) {
            throw new ExternalServiceException("Falha ao obter país ou cachorro");
        }

        var response = countryDogAdapter.toWire(dog, country);

        cacheService.set(cacheKey, response);
        return ResponseEntity.ok(response);
    }
}