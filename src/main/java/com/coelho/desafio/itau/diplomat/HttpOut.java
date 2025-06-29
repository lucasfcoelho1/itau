package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.adapter.wire.CountryWireIn;
import com.coelho.desafio.itau.model.Country;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.Optional;

@Service
public class HttpOut {

    private final RestClient builder;
    CountryAdapter countryAdapter;

    public HttpOut(RestClient.Builder builder, CountryAdapter countryAdapter) {
        this.builder = builder.baseUrl("https://restcountries.com/v3.1").build() ;
        this.countryAdapter = countryAdapter;
    }

    public Optional<Country> fetchByName(String name) {
        try {
            String uri = UriComponentsBuilder.fromPath("/name/{country}")
                    .buildAndExpand(name)
                    .toUriString();

            CountryWireIn[] response = builder
                    .get()
                    .uri(uri)
                    .retrieve()
                    .body(CountryWireIn[].class);

            return Optional.ofNullable(response)
                    .filter(arr -> arr.length > 0)
                    .map(arr -> countryAdapter.mapToCountry(arr[0]));
        } catch (RestClientException ex) {
            System.err.println(ex.getMessage());
            return Optional.empty();
        }
    }

    private Country mapToEntity(CountryWireIn api) {
        return new Country(
                api.name.common,
                api.region,
                api.population,
                api.flags != null ? api.flags.png : null
        );
    }
}
