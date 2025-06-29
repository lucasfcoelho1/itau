package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.adapter.DogAdapter;
import com.coelho.desafio.itau.diplomat.wire.CountryWireIn;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Dog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

@Service
public class HttpOut {

    private final RestClient countriesClient;
    private final RestClient deepSeekClient;
    private final CountryAdapter countryAdapter;
    private final DogAdapter dogAdapter;

    public HttpOut(
            RestClient.Builder builder,
            CountryAdapter countryAdapter,
            @Value("${deepseek.api.key}") String apiKey, DogAdapter dogAdapter
    ) {
        this.countriesClient = builder
                .baseUrl("https://restcountries.com/v3.1")
                .build();
        this.dogAdapter = dogAdapter;

        this.deepSeekClient = builder
                .baseUrl("https://openrouter.ai")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();

        this.countryAdapter = countryAdapter;
    }

    public Country fetchCountryByName(String name) {
        try {
            String uri = UriComponentsBuilder.fromPath("/name/{country}")
                    .buildAndExpand(name)
                    .toUriString();

            CountryWireIn[] response = countriesClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(CountryWireIn[].class);

            if (response != null && response.length > 0) {
                CountryWireIn countryWire = response[0];
                return countryAdapter.toModel(countryWire);
            }

        } catch (RestClientException ex) {
            System.err.println("Erro ao buscar país: " + ex.getMessage());
        }

        return null;
    }

    public Dog fetchDogSuggestionByCountryPrompt(String prompt) {
        try {
            Map<String, Object> requestBody = getStringObjectMap(prompt);

            Map<String, Object> response = deepSeekClient
                    .post()
                    .uri("/api/v1/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");

            if (choices != null && !choices.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                if (message != null) {
                    String content = (String) message.get("content");
                    return dogAdapter.toModel(content);
                }
            }

        } catch (RestClientException ex) {
            System.err.println("Erro ao chamar IA: " + ex.getMessage());
        }

        return null;
    }

    private static Map<String, Object> getStringObjectMap(String prompt) {
        return Map.of(
                "model", "deepseek/deepseek-r1-0528:free",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                )
        );
    }

    public record DeepSeekMessage(String role, String content) {
    }

    public record DeepSeekRequest(String model, List<DeepSeekMessage> messages) {
    }
}
