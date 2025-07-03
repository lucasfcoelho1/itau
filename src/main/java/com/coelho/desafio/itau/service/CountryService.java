package com.coelho.desafio.itau.service;

import com.coelho.desafio.itau.diplomat.HttpOut;
import com.coelho.desafio.itau.logic.CacheKey;
import com.coelho.desafio.itau.model.Country;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

@Service
public class CountryService {
    private final HttpOut httpOut;
    private final CacheService cacheService;

    public CountryService(HttpOut httpOut, CacheService cacheService) {
        this.httpOut = httpOut;
        this.cacheService = cacheService;
    }

    public Country fetchCountryByName(String countryName) {
        return httpOut.fetchCountryByName(countryName);
    }

    public List<?> getCountryListCached() {
        return cacheService.get(CacheKey.COUNTRY_LIST_ALL.getDisplayName(), List.class);
    }

    public List<Country> mappedCachedCountries(List<?> countryListCached) {
        return countryListCached.stream()
                .filter(Objects::nonNull)
                .map(obj -> (LinkedHashMap<?, ?>) obj)
                .map(map -> new Country(
                        (String) map.get("title"),
                        (String) map.get("region")
                ))
                .toList();
    }

    public List<Country> fetchCountries() {
        return httpOut.fetchCountryList();
    }
}