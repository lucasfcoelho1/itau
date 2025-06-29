package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.adapter.wire.CountryWireIn;
import com.coelho.desafio.itau.service.CountryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HttpOut {

    private final RestClient countriesClient;
    private final RestClient deepSeekClient;
    private final CountryAdapter countryAdapter;

    public HttpOut(
            RestClient.Builder builder,
            CountryAdapter countryAdapter,
            @Value("${deepseek.api.key}") String apiKey
    ) {
        this.countriesClient = builder
                .baseUrl("https://restcountries.com/v3.1")
                .build();

        this.deepSeekClient = builder
                .baseUrl("https://openrouter.ai")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();

        this.countryAdapter = countryAdapter;
    }

    public Optional<CountryWireIn> fetchCountryByName(String name) {
        try {
            String uri = UriComponentsBuilder.fromPath("/name/{country}")
                    .buildAndExpand(name)
                    .toUriString();

            CountryWireIn[] response = countriesClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(CountryWireIn[].class);

            return Optional.ofNullable(response)
                    .filter(countries -> countries.length > 0)
                    .map(countries -> countries[0]);

        } catch (RestClientException ex) {
            System.err.println("Erro ao buscar país: " + ex.getMessage());
            return Optional.empty();
        }
    }


    public Optional<String> fetchDogSuggestionByCountryPrompt(String prompt) {
        try {

            DeepSeekRequest requestBody = new DeepSeekRequest(
                    "deepseek/deepseek-r1-0528:free",
                    List.of(new DeepSeekMessage("user", prompt))
            );

            @SuppressWarnings("unchecked") Map<String, Object> response = deepSeekClient
                    .post()
                    .uri("/api/v1/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            // extrair resposta
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");

            if (choices != null && !choices.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

                if (message != null) {
                    return Optional.ofNullable((String) message.get("content"));
                }
            }

        } catch (RestClientException ex) {
            System.err.println("Erro ao chamar IA: " + ex.getMessage());
        }

        return Optional.empty();
    }

    public record DeepSeekMessage(String role, String content) {
    }

    public record DeepSeekRequest(String model, List<DeepSeekMessage> messages) {
    }
}
