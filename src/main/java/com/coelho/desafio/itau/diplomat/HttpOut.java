package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.adapter.PetAdapter;
import com.coelho.desafio.itau.diplomat.wire.in.CountryWireIn;
import com.coelho.desafio.itau.exception.ExternalServiceException;
import com.coelho.desafio.itau.exception.RateLimitExceededException;
import com.coelho.desafio.itau.exception.ResponseParseException;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Pet;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class HttpOut {

    private static final Logger log = LoggerFactory.getLogger(HttpOut.class);

    private final RestClient countriesClient;
    private final RestClient aiClient;
    private final CountryAdapter countryAdapter;
    private final PetAdapter petAdapter;

    public HttpOut(RestClient.Builder builder, CountryAdapter countryAdapter, @Value("${openrouter.api.key}") String apiKey, PetAdapter petAdapter) {
        this.countriesClient = builder.baseUrl("https://restcountries.com/v3.1").build();
        this.aiClient = builder.baseUrl("https://openrouter.ai")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.countryAdapter = countryAdapter;
        this.petAdapter = petAdapter;
    }

    @Retryable(maxAttempts = 2, backoff = @Backoff(delay = 2000, multiplier = 2))
    public List<Country> fetchCountryList() {
        return Optional.ofNullable(
                        countriesClient.get()
                                .uri("https://restcountries.com/v3.1/all?fields=name,region,population,flags")
                                .retrieve()
                                .body(CountryWireIn[].class)
                ).stream().flatMap(Arrays::stream)
                .map(countryAdapter::toModel)
                .toList()
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            if (list.isEmpty()) {
                                throw new ExternalServiceException("Nenhum país foi encontrado na API externa.");
                            }
                            return list;
                        }
                ));
    }

    @Retryable(retryFor = ExternalServiceException.class, maxAttempts = 2, backoff = @Backoff(delay = 2000, multiplier = 2))
    public Country fetchCountryByName(String countryName) {
        return Optional.ofNullable(countryName)
                .map(n -> UriComponentsBuilder.fromPath("/name/{country}").buildAndExpand(n).toUriString())
                .map(this::fetchCountryWireOrThrow)
                .filter(arr -> arr.length > 0)
                .map(arr -> arr[0])
                .map(countryAdapter::toModel)
                .orElseThrow(() -> new ExternalServiceException("Nenhum país encontrado com o nome fornecido."));
    }

    @Retryable(retryFor = ExternalServiceException.class, maxAttempts = 2, backoff = @Backoff(delay = 2000, multiplier = 2))
    @SuppressWarnings("unchecked")
    public Pet fetchPetSuggestionByCountryPrompt(String prompt) {
        return Optional.ofNullable(prompt)
                .map(p -> Map.of(
                        "model", "deepseek/deepseek-r1:free",
                        "messages", List.of(Map.of("role", "user", "content", p))
                ))
                .map(this::callAiWithCircuitBreaker)
                .map(response -> (List<Map<String, Object>>) response.get("choices"))
                .filter(choices -> !choices.isEmpty())
                .map(choices -> (Map<String, Object>) choices.get(0).get("message"))
                .map(message -> (String) message.get("content"))
                .map(this::parsePetOrThrow)
                .orElseThrow(() -> new ExternalServiceException("Resposta da IA estava vazia ou inválida"));
    }

    private CountryWireIn[] fetchCountryWireOrThrow(String uri) {
        try {
            return countriesClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(CountryWireIn[].class);
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Erro ao buscar país", ex);
        }
    }

    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackAi")
    private Map<String, Object> callAiWithCircuitBreaker(Map<String, Object> requestBody) {
        return callAiOrThrow(requestBody);
    }

    private Map<String, Object> callAiOrThrow(Map<String, Object> requestBody) {
        try {
            return aiClient.post()
                    .uri("/api/v1/chat/completions")
                    .body(requestBody)
                    .retrieve()
                    .onStatus(status -> status.value() == 429, (req, res) -> {
                        throw new RateLimitExceededException("Limite de requisições atingido na IA");
                    })
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RateLimitExceededException e) {
            throw e;
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Erro ao chamar IA", ex);
        }
    }

    private Pet parsePetOrThrow(String content) {
        try {
            return petAdapter.toModel(content);
        } catch (Exception e) {
            throw new ResponseParseException("Erro ao converter resposta da IA para Pet", e);
        }
    }

    @Recover
    public Country recoverCountry(ExternalServiceException ex, String name) {
        log.error("Falha ao buscar país '{}' após múltiplas tentativas", name, ex);
        throw new ExternalServiceException("Falha definitiva ao buscar país: " + name, ex);
    }

    @Recover
    public Pet recoverPet(ExternalServiceException ex, String prompt) {
        log.error("Falha ao obter sugestão de cachorro com prompt '{}' após múltiplas tentativas", prompt, ex);
        throw new ExternalServiceException("Falha definitiva ao obter sugestão de cachorro da IA", ex);
    }

    @Recover
    public Pet recoverRateLimit(RateLimitExceededException ex, String prompt) {
        log.error("Limite de requisições atingido com prompt '{}'", prompt, ex);
        throw new RateLimitExceededException("Falha definitiva: limite de requisições atingido na IA", ex);
    }

    private Map<String, Object> fallbackAi(Map<String, Object> requestBody, Throwable t) {
        throw new ExternalServiceException("Serviço de IA indisponível no momento (Circuit Breaker aberto)", t);
    }
}