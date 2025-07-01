package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.adapter.DogAdapter;
import com.coelho.desafio.itau.diplomat.wire.in.CountryWireIn;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Dog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Retryable(
        maxAttempts = 2,
        backoff = @Backoff(delay = 2000, multiplier = 2))
public class HttpOut {
    private final RestClient countriesClient;
    private final RestClient deepSeekClient;
    private final CountryAdapter countryAdapter;
    private final DogAdapter dogAdapter;

    public HttpOut(
            RestClient.Builder builder,
            CountryAdapter countryAdapter,
            @Value("${openrouter.api.key}") String apiKey, DogAdapter dogAdapter
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
        return Optional.ofNullable(name)
                .map(n -> UriComponentsBuilder.fromPath("/name/{country}")
                        .buildAndExpand(n)
                        .toUriString())
                .map(this::safeFetchFromApi)
                .filter(arr -> arr.length > 0)
                .map(arr -> arr[0])
                .map(countryAdapter::toModel)
                .orElse(null);
    }

    public Dog fetchDogSuggestionByCountryPrompt(String prompt) {
        Dog fallback = new Dog();
        fallback.setDescription("Ocorreu um erro na sugestão da IA, por favor tente novamente");

        //noinspection ConstantValue,unchecked
        return Optional.of(prompt)
                .map(p -> Map.of(
                        "model", "deepseek/deepseek-r1:free",
                        "messages", List.of(Map.of("role", "user", "content", p))
                ))
                .map(this::callDeepSeek)
                .map(response -> (List<Map<String, Object>>) response.get("choices"))
                .filter(choices -> !choices.isEmpty())
                .map(choices -> (Map<String, Object>) choices.get(0).get("message"))
                .map(message -> (String) message.get("content"))
                .map(this::safeParseDog)
                .filter(Objects::nonNull)
                .orElse(fallback);
    }

    private CountryWireIn[] safeFetchFromApi(String uri) {
        try {
            return countriesClient
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(CountryWireIn[].class);
        } catch (RestClientException ex) {
            System.err.println("Erro ao buscar país: " + ex.getMessage());
            return new CountryWireIn[0];
        }
    }

    private Map<String, Object> callDeepSeek(Map<String, Object> requestBody) {
        try {
            return deepSeekClient
                    .post()
                    .uri("/api/v1/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (RestClientException ex) {
            System.err.println("Erro ao chamar IA: " + ex.getMessage());
            return null;
        }
    }

    private Dog safeParseDog(String content) {
        try {
            return dogAdapter.toModel(content);
        } catch (Exception e) {
            System.err.println("Erro ao converter resposta da IA para Dog: " + e.getMessage());
            return null;
        }
    }

    @Recover
    public void recover(){
        System.out.println("Tentando novamente...");
    }

    public record DeepSeekMessage(String role, String content) {
    }

    public record DeepSeekRequest(String model, List<DeepSeekMessage> messages) {
    }
}
