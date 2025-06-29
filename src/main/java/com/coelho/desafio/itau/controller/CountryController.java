package com.coelho.desafio.itau.controller;

import com.coelho.desafio.itau.diplomat.HttpOut;
import com.coelho.desafio.itau.model.Country;
import org.springframework.stereotype.Service;

@Service
public class CountryController {

    private final HttpOut httpOut;

    public CountryController(HttpOut httpOut) {
        this.httpOut = httpOut;
    }

    public Country getCountry(String countryName) {
        return httpOut.fetchCountryByName(countryName);
    }
}
