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

//    public List<CountryWireOut> getAllCountriesWithFilter(CountryWireIn filter) {
//        return getCountryWireOuts(filter);
//    }

//    private List<CountryWireOut> getCountryWireOuts(CountryWireIn filter) {
//        String cacheKey = "countries:v1";
//
//        List<CountryWireOut> allCountries = cacheService.get(cacheKey, List.class);
//
//        if (allCountries == null) {
//            allCountries = httpOut.fetchAllCountriesFromExternal();
//            cacheService.set(cacheKey, allCountries); // TTL de 1 hora já configurado
//        }
//
//        // Filtro in-memory
//        return allCountries.stream()
//                .filter(c -> filter.getRegion() == null || c.getRegion().equalsIgnoreCase(filter.getRegion()))
//                .filter(c -> filter.getName() == null || c.getName().toLowerCase().contains(filter.getName().common.toLowerCase()))
//                .toList();
//    }
}
