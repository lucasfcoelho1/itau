package com.coelho.desafio.itau.service;

import com.coelho.desafio.itau.diplomat.HttpOut;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.repository.CountryRepository;
import org.springframework.stereotype.Service;

@Service
public class CountryService {
    private final HttpOut httpOut;
    private final CountryRepository countryRepository;

    public CountryService(HttpOut httpOut, CountryRepository countryRepository) {
        this.httpOut = httpOut;
        this.countryRepository = countryRepository;
    }

    public Country fetchCountryByName(String countryName) {
        var existingByInput = countryRepository.findByTitleIgnoreCase(countryName);
        if (existingByInput.isPresent()) {
            return existingByInput.get();
        }

        var countryFromApi = httpOut.fetchCountryByName(countryName);
        if (countryFromApi != null) {
            var existingByApiTitle = countryRepository.findByTitleIgnoreCase(countryFromApi.getTitle());
            if (existingByApiTitle.isPresent()) {
                return existingByApiTitle.get();
            }

            countryRepository.save(countryFromApi);
            return countryFromApi;
        }
        return null;
    }
}
