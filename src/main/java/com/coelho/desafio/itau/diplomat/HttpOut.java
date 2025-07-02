package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.adapter.DogAdapter;
import com.coelho.desafio.itau.diplomat.wire.in.CountryWireIn;
import com.coelho.desafio.itau.exception.ExternalServiceException;
import com.coelho.desafio.itau.exception.RateLimitExceededException;
import com.coelho.desafio.itau.exception.ResponseParseException;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Dog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.retry.annotation.Backoff;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HttpOut {

    private static final Logger log = LoggerFactory.getLogger(HttpOut.class);

    private final RestClient countriesClient;
    private final RestClient deepSeekClient;
    private final CountryAdapter countryAdapter;
    private final DogAdapter dogAdapter;

    public HttpOut(RestClient.Builder builder, CountryAdapter countryAdapter, @Value("${openrouter.api.key}") String apiKey, DogAdapter dogAdapter) {
        this.countriesClient = builder.baseUrl("https://restcountries.com/v3.1").build();
        this.deepSeekClient = builder.baseUrl("https://openrouter.ai")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .build();
        this.countryAdapter = countryAdapter;
        this.dogAdapter = dogAdapter;
    }

    @Retryable(retryFor = ExternalServiceException.class, maxAttempts = 2, backoff = @Backoff(delay = 2000, multiplier = 2))
    public Country fetchCountryByName(String name) {
        return Optional.ofNullable(name)
                .map(n -> UriComponentsBuilder.fromPath("/name/{country}").buildAndExpand(n).toUriString())
                .map(this::fetchCountryWireOrThrow)
                .filter(arr -> arr.length > 0)
                .map(arr -> arr[0])
                .map(countryAdapter::toModel)
                .orElseThrow(() -> new ExternalServiceException("Nenhum país encontrado com o nome fornecido."));
    }

    @Retryable(retryFor = ExternalServiceException.class, maxAttempts = 2, backoff = @Backoff(delay = 2000, multiplier = 2))
    @SuppressWarnings("unchecked")
    public Dog fetchDogSuggestionByCountryPrompt(String prompt) {
        return Optional.ofNullable(prompt)
                .map(p -> Map.of(
                        "model", "deepseek/deepseek-r1:free",
                        "messages", List.of(Map.of("role", "user", "content", p))
                ))
                .map(this::callDeepSeekWithCircuitBreaker)
                .map(response -> (List<Map<String, Object>>) response.get("choices"))
                .filter(choices -> !choices.isEmpty())
                .map(choices -> (Map<String, Object>) choices.get(0).get("message"))
                .map(message -> (String) message.get("content"))
                .map(this::parseDogOrThrow)
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

    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackDeepSeek")
    private Map<String, Object> callDeepSeekWithCircuitBreaker(Map<String, Object> requestBody) {
        return callDeepSeekOrThrow(requestBody);
    }

    private Map<String, Object> callDeepSeekOrThrow(Map<String, Object> requestBody) {
        try {
            return deepSeekClient.post()
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

    private Dog parseDogOrThrow(String content) {
        try {
            return dogAdapter.toModel(content);
        } catch (Exception e) {
            throw new ResponseParseException("Erro ao converter resposta da IA para Dog", e);
        }
    }

    @Recover
    public Country recoverCountry(ExternalServiceException ex, String name) {
        log.error("Falha ao buscar país '{}' após múltiplas tentativas", name, ex);
        throw new ExternalServiceException("Falha definitiva ao buscar país: " + name, ex);
    }

    @Recover
    public Dog recoverDog(ExternalServiceException ex, String prompt) {
        log.error("Falha ao obter sugestão de cachorro com prompt '{}' após múltiplas tentativas", prompt, ex);
        throw new ExternalServiceException("Falha definitiva ao obter sugestão de cachorro da IA", ex);
    }

    @Recover
    public Dog recoverRateLimit(RateLimitExceededException ex, String prompt) {
        log.error("Limite de requisições atingido com prompt '{}'", prompt, ex);
        throw new RateLimitExceededException("Falha definitiva: limite de requisições atingido na IA", ex);
    }

    private Map<String, Object> fallbackDeepSeek(Map<String, Object> requestBody, Throwable t) {
        throw new ExternalServiceException("Serviço de IA indisponível no momento (Circuit Breaker aberto)", t);
    }
}