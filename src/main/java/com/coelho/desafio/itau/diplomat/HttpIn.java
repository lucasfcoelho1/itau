package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.adapter.PetSuggestionAdapter;
import com.coelho.desafio.itau.controller.CountryController;
import com.coelho.desafio.itau.controller.PetController;
import com.coelho.desafio.itau.diplomat.wire.out.CountryWireOut;
import com.coelho.desafio.itau.diplomat.wire.out.PetSuggestionWireOut;
import com.coelho.desafio.itau.service.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HttpIn {

    private final PetController petController;
    private final PetSuggestionAdapter petSuggestionAdapter;
    private final CountryController countryController;
    private final CountryAdapter countryAdapter;
    private final CacheService cacheService;
    private final HttpOut httpOut;

    public HttpIn(PetController petController, PetSuggestionAdapter petSuggestionAdapter, CountryController countryController, CountryAdapter countryAdapter, CacheService cacheService, HttpOut httpOut) {
        this.petController = petController;
        this.petSuggestionAdapter = petSuggestionAdapter;
        this.countryController = countryController;
        this.countryAdapter = countryAdapter;
        this.cacheService = cacheService;
        this.httpOut = httpOut;
    }

    @GetMapping("/api/countries")
    public ResponseEntity<?> getCountries(@RequestParam(required = false) String region, @RequestParam(required = false) String name) {
        var countries = countryController.getCountries(name, region);

        if (countries.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<CountryWireOut> response = countries.stream()
                .map(countryAdapter::toWireOut)
                .toList();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/api/pet-suggestion/country/{countryName}")
    public ResponseEntity<PetSuggestionWireOut> getPetSuggestionResponseByCountryName(@PathVariable String countryName) {
        var petSuggestion = petController.generatePetSuggestionByCountryName(countryName);

        var response = petSuggestionAdapter.toWire(petSuggestion);

        return ResponseEntity.ok(response);
    }
}