package com.coelho.desafio.itau.service;

import com.coelho.desafio.itau.diplomat.HttpOut;
import com.coelho.desafio.itau.exception.ExternalServiceException;
import com.coelho.desafio.itau.logic.CacheKey;
import com.coelho.desafio.itau.model.Country;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        final String cacheKey = CacheKey.COUNTRY_LIST_ALL.getDisplayName();

        List<?> countryListCached = cacheService.get(cacheKey, List.class);
        return countryListCached;
    }

    public List<Country> mappedCachedCountries(List<?> countryListCached) {
        return countryListCached.stream().filter(java.util.Objects::nonNull).map(obj -> {
            java.util.LinkedHashMap<?, ?> map = (java.util.LinkedHashMap<?, ?>) obj;
            return new Country(
                    (String) map.get("title"),
                    (String) map.get("region")
            );
        }).collect(Collectors.toList());
    }

    public List<Country> fetchCountries(){
        try {
            // Chamada para a API externa

            List<Country> countries = httpOut.getCountryList();
            if (countries == null || countries.isEmpty()) {
                throw new ExternalServiceException("Nenhum país foi encontrado na API externa.");
            }
            return countries;


        }catch (ExternalServiceException e){
            throw e;
        }
    }
}