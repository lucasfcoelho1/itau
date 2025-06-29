package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.adapter.wire.CountryWireIn;
import com.coelho.desafio.itau.controller.CountryController;
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

    private final HttpOut httpOut;
    private final CountryController countryController;
    private final CountryAdapter countryAdapter;

    public HttpIn(HttpOut httpOut, CountryController countryController, CountryAdapter countryAdapter) {
        this.httpOut = httpOut;
        this.countryController = countryController;
        this.countryAdapter = countryAdapter;
    }

    @GetMapping
    public ResponseEntity<Country> getCountryByName(@RequestParam String name) {
        Optional<CountryWireIn> countryWireIn = httpOut.fetchCountryByName(name);

        Optional<Country>  country = countryWireIn.map(countryAdapter::toModel);

        return country
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
