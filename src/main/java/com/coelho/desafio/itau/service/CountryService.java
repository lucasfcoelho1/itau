package com.coelho.desafio.itau.service;

import com.coelho.desafio.itau.diplomat.HttpOut;
import com.coelho.desafio.itau.model.Country;
import org.springframework.stereotype.Service;

@Service
public class CountryService {
    private final HttpOut httpOut;

    public CountryService(HttpOut httpOut) {
        this.httpOut = httpOut;
    }

    public Country fetchCountryByName(String countryName) {
        return httpOut.fetchCountryByName(countryName);
    }
}