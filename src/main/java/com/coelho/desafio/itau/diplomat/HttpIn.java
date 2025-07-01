package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryDogAdapter;
import com.coelho.desafio.itau.controller.CountryController;
import com.coelho.desafio.itau.controller.DogController;
import com.coelho.desafio.itau.diplomat.wire.out.CountryDogWireOut;
import com.coelho.desafio.itau.service.CacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

@RestController
public class HttpIn {

    private final DogController dogController;
    private final CountryController countryController;
    private final CountryDogAdapter countryDogAdapter;
    private final CacheService cacheService;

    public HttpIn(DogController dogController, CountryController countryController, CountryDogAdapter countryDogAdapter, Jedis jedis, ObjectMapper objectMapper, CacheService cacheService) {
        this.dogController = dogController;
        this.countryController = countryController;
        this.countryDogAdapter = countryDogAdapter;
        this.cacheService = cacheService;
    }

    @GetMapping("api/countries/{countryName}/dogs")
    public ResponseEntity<CountryDogWireOut> getDogSuggestionByCountryName(@PathVariable String countryName) {
        String cacheKey = "countryDog:" + countryName.toLowerCase();

        var cached = cacheService.get(cacheKey, CountryDogWireOut.class);
        if (cached != null) {
            return ResponseEntity.ok(cached);
        }

        var countryResponse = countryController.getCountry(countryName);
        var dogResponse = dogController.getDogAiSuggestion(countryResponse);
        var countryDogWireOut = countryDogAdapter.toWire(dogResponse, countryResponse);

        if (countryDogWireOut != null) {
            cacheService.set(cacheKey, countryDogWireOut);
        }
        return ResponseEntity.ok(countryDogWireOut);
    }
}
