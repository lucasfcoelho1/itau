package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.model.Country;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/countries")
public class HttpIn {

    private final HttpOut countryService;

    public HttpIn(HttpOut httpOut) {
        this.countryService = httpOut;
    }

    @GetMapping
    public ResponseEntity<Country> getCountryByName(@RequestParam String name) {
        Optional<Country> country = countryService.fetchByName(name);

        return country
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
