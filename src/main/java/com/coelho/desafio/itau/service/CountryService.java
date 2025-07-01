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
        // Primeiro, tenta encontrar o nome digitado
        var existingByInput = countryRepository.findByTitleIgnoreCase(countryName);
        if (existingByInput.isPresent()) {
            return existingByInput.get();
        }

        // Se não encontrou, busca na API
        var countryFromApi = httpOut.fetchCountryByName(countryName);
        if (countryFromApi != null) {
            // Verifica se já existe no banco pelo nome retornado pela API ("Brazil", por exemplo)
            var existingByApiTitle = countryRepository.findByTitleIgnoreCase(countryFromApi.getTitle());
            if (existingByApiTitle.isPresent()) {
                return existingByApiTitle.get(); // evita duplicar
            }

            // Agora sim, salva com segurança
            countryRepository.save(countryFromApi);
            return countryFromApi;
        }
        return null;
    }
}
