package com.coelho.desafio.itau.controller;

import com.coelho.desafio.itau.logic.CacheKey;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.service.CacheService;
import com.coelho.desafio.itau.service.CountryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryController {

    private final CountryService countryService;
    private final CacheService cacheService;

    public CountryController(CountryService countryService, CacheService cacheService) {
        this.cacheService = cacheService;
        this.countryService = countryService;
    }

    public Country getCountry(String countryName) {
        return countryService.fetchCountryByName(countryName);
    }

    public List<Country> getCountries(String name, String region) {
        return Optional.ofNullable(countryService.getCountryListCached())
                .map(countryService::mappedCachedCountries)
                .orElseGet(() -> {
                    var countries = countryService.fetchCountries();
                    cacheService.set(CacheKey.COUNTRY_LIST_ALL.getDisplayName(), countries);
                    return countries;
                })
                .stream()
                .filter(c -> region == null || Optional.ofNullable(c.getRegion())
                        .map(r -> r.equalsIgnoreCase(region)).orElse(false))
                .filter(c -> name == null || Optional.ofNullable(c.getTitle())
                        .map(t -> t.toLowerCase().contains(name.toLowerCase())).orElse(false))
                .toList();
    }
}
