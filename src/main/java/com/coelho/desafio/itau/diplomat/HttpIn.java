package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.PetSuggestionAdapter;
import com.coelho.desafio.itau.controller.CountryController;
import com.coelho.desafio.itau.controller.PetController;
import com.coelho.desafio.itau.diplomat.wire.in.CountryWireIn;
import com.coelho.desafio.itau.diplomat.wire.out.PetSuggestionWireOut;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.service.CacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class HttpIn {

    private final PetController petController;
    private final PetSuggestionAdapter petSuggestionAdapter;
    private final CountryController countryController;
    private final CacheService cacheService;
    private final HttpOut httpOut;

    public HttpIn(PetController petController, PetSuggestionAdapter petSuggestionAdapter, CountryController countryController, CacheService cacheService, HttpOut httpOut) {
        this.petController = petController;
        this.petSuggestionAdapter = petSuggestionAdapter;
        this.countryController = countryController;
        this.cacheService = cacheService;
        this.httpOut = httpOut;
    }

    @GetMapping("/api/countries")
    public ResponseEntity<?> getCountries(@RequestParam(required = false) String region, @RequestParam(required = false) String name) {
        // Chave fixa do cache, já que estamos cacheando todos os países
        final String cacheKey = "countryList:all:";

        // Verifica se há no cache
        List<?> rawList = cacheService.get(cacheKey, List.class);
        List<Country> countries = null;
        if (rawList != null) {
            countries = rawList.stream().filter(java.util.Objects::nonNull).map(obj -> {
                java.util.LinkedHashMap<?, ?> map = (java.util.LinkedHashMap<?, ?>) obj;
                return new Country(
                        (String) map.get("title"),
                        (String) map.get("region")
                );
            }).collect(Collectors.toList());
        }
        if (countries == null) {
            try {
                // Chamada para a API externa
                CountryWireIn[] countryWireIns = httpOut.getListCountry();

                if (countryWireIns == null || countryWireIns.length == 0) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum país foi encontrado na API externa.");
                }

                // Mapeia os dados externos para o modelo interno
                countries = Arrays.stream(countryWireIns).map(wire -> new Country(wire.getName().common, wire.getRegion())).collect(Collectors.toList());

                // Salva no cache
                cacheService.set(cacheKey, countries);
            } catch (RestClientException ex) {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Erro ao acessar a API externa de países.");
            }
        }

        // Filtros
        Stream<Country> stream = countries.stream();
        if (region != null) {
            stream = stream.filter(c -> c.getRegion() != null && c.getRegion().equalsIgnoreCase(region));
        }
        if (name != null) {
            stream = stream.filter(c -> c.getTitle() != null && c.getTitle().toLowerCase().contains(name.toLowerCase()));
        }

        List<Country> filtered = stream.toList();

        if (filtered.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum país encontrado com os filtros fornecidos.");
        }

        return ResponseEntity.ok(filtered);
    }


    @GetMapping("/api/pet-suggestion/country/{countryName}")
    public ResponseEntity<PetSuggestionWireOut> getPetSuggestionResponseByCountryName(@PathVariable String countryName) {
        var petSuggestion = petController.generatePetSuggestionByCountryName(countryName);

        var response = petSuggestionAdapter.toWire(petSuggestion);

        return ResponseEntity.ok(response);
    }
}