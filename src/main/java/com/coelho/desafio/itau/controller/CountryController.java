package com.coelho.desafio.itau.controller;

import com.coelho.desafio.itau.diplomat.HttpOut;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.service.CountryService;
import org.springframework.stereotype.Service;

@Service
public class CountryController {

    private final HttpOut httpOut;
    private final CountryService countryService;

    public CountryController(HttpOut httpOut, CountryService countryService) {
        this.httpOut = httpOut;
        this.countryService = countryService;
    }

    public Country getCountry(String countryName) {
        return countryService.fetchCountryByName(countryName);
    }
}
