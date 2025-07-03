package com.coelho.desafio.itau.controller;

import com.coelho.desafio.itau.logic.CacheKey;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.service.CacheService;
import com.coelho.desafio.itau.service.CountryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

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
        var countryListCached = countryService.getCountryListCached();

        if (countryListCached != null) {
            return filterCountries(countryService.mappedCachedCountries(countryListCached), name, region);
        }

        var countries = countryService.fetchCountries();

        cacheService.set(CacheKey.COUNTRY_LIST_ALL.getDisplayName(), countries);

        return filterCountries(countries, name, region);
    }

    private List<Country> filterCountries(List<Country> countries, String name, String region) {
        Stream<Country> stream = countries.stream();
        if (region != null) {
            stream = stream.filter(c -> c.getRegion() != null && c.getRegion().equalsIgnoreCase(region));
        }
        if (name != null) {
            stream = stream.filter(c -> c.getTitle() != null && c.getTitle().toLowerCase().contains(name.toLowerCase()));
        }

        List<Country> filtered = stream.toList();

        return filtered;
    }


}
