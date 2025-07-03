package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.adapter.PetSuggestionAdapter;
import com.coelho.desafio.itau.controller.CountryController;
import com.coelho.desafio.itau.controller.PetController;
import com.coelho.desafio.itau.diplomat.wire.out.CountryWireOut;
import com.coelho.desafio.itau.diplomat.wire.out.PetSuggestionWireOut;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class HttpIn {

    private final PetController petController;
    private final PetSuggestionAdapter petSuggestionAdapter;
    private final CountryController countryController;
    private final CountryAdapter countryAdapter;

    public HttpIn(PetController petController, PetSuggestionAdapter petSuggestionAdapter, CountryController countryController, CountryAdapter countryAdapter) {
        this.petController = petController;
        this.petSuggestionAdapter = petSuggestionAdapter;
        this.countryController = countryController;
        this.countryAdapter = countryAdapter;
    }

    @GetMapping("/api/countries")
    public ResponseEntity<List<CountryWireOut>> getCountries(
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String name) {

        return Optional.of(countryController.getCountries(name, region))
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .map(countryAdapter::toWireOut)
                        .toList())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/api/pet-suggestion/country/{countryName}")
    public ResponseEntity<PetSuggestionWireOut> getPetSuggestionResponseByCountryName(
            @PathVariable String countryName) {

        return Optional.ofNullable(petController.generatePetSuggestionByCountryName(countryName))
                .map(petSuggestionAdapter::toWireOut)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}