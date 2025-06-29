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
    public ResponseEntity<Country> getCountryByName(@RequestParam String name) {
        return httpOut.fetchCountryByName(name)
                //adapt to model
                .map(countryAdapter::toModel)
                .map(country -> {
                    String prompt = generatePrompt(country);
                    httpOut.fetchDogSuggestionByCountryPrompt(prompt)
                            .ifPresent(resposta -> {
                                System.out.println("🐶 IA sugeriu: " + resposta);
                                // aqui você pode salvar, retornar no body, etc.
                            });

                    return ResponseEntity.ok(country);
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
}
