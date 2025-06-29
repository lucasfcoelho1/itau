package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.controller.DogController;
import com.coelho.desafio.itau.model.Country;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/countries")
public class HttpIn {

    private final HttpOut httpOut;
    private final DogController dogController;
    private final CountryAdapter countryAdapter;

    public HttpIn(HttpOut httpOut, DogController dogController, CountryAdapter countryAdapter) {
        this.httpOut = httpOut;
        this.dogController = dogController;
        this.countryAdapter = countryAdapter;
    }

    @GetMapping
    public ResponseEntity<CountryWithSuggestion> getCountryByName(@RequestParam String name) {
        return httpOut.fetchCountryByName(name)
                .map(countryAdapter::toModel)
                .map(country -> {
                    String prompt = generatePrompt(country);
                    String dogSuggestion = httpOut
                            .fetchDogSuggestionByCountryPrompt(prompt)
                            .orElse("Nenhuma sugestão de cachorro disponível 🐾");

                    return ResponseEntity.ok(new CountryWithSuggestion(country, dogSuggestion));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private String generatePrompt(Country country) {
        return """
        Me sugira uma raça de cachorro ideal para alguém que vive em um país com as seguintes características:

        Nome do país: %s
        Região: %s
        População total: %d
        Estilo de vida, clima e ambiente devem ser inferidos com base nesses dados.

        Responda apenas com o nome da raça ideal e uma frase explicando o motivo.
        """.formatted(
                country.getTitle(),
                country.getRegion(),
                country.getTotalPopulation()
        );
    }

    public static class CountryWithSuggestion {
        private final Country country;
        private final String dogSuggestion;

        public CountryWithSuggestion(Country country, String dogSuggestion) {
            this.country = country;
            this.dogSuggestion = dogSuggestion;
        }

        public Country getCountry() {
            return country;
        }

        public String getDogSuggestion() {
            return dogSuggestion;
        }
    }
}
