package com.coelho.desafio.itau.diplomat;

import com.coelho.desafio.itau.adapter.CountryAdapter;
import com.coelho.desafio.itau.adapter.CountryDogAdapter;
import com.coelho.desafio.itau.controller.CountryController;
import com.coelho.desafio.itau.controller.DogController;
import com.coelho.desafio.itau.diplomat.wire.CountryDogWireOut;
import com.coelho.desafio.itau.logic.AiSuggestionLogic;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Dog;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HttpInTest {

    @Test
    void shouldReturnCountryAndDogSuccessfully() {
        // mocks
        HttpOut httpOut = mock(HttpOut.class);
        AiSuggestionLogic aiSuggestionLogic = mock(AiSuggestionLogic.class);

        DogController dogController = mock(DogController.class);
        CountryController countryController = mock(CountryController.class);
        CountryDogAdapter countryDogAdapter = mock(CountryDogAdapter.class);

        // mock da classe que estamos testando
        HttpIn httpIn = new HttpIn(dogController, countryController, countryDogAdapter);

        // dados simulados
        Country country = new Country("France", "Europe", 67391582, "https://flagcdn.com/fr.png");
        Dog dog = new Dog("Golden Retriever", "Excelente para famílias ativas e climas temperados");
        CountryDogWireOut countryDogWireOut = new CountryDogWireOut(
                country,
                dog);

        // comportamento dos mocks
        when(countryController.getCountry("France")).thenReturn(country);
        when(dogController.getDogAiSuggestion(country)).thenReturn(dog);
        when(countryDogAdapter.toWire(dog, country)).thenReturn(countryDogWireOut);

        // chamada
        ResponseEntity<?> response = httpIn.getDogSuggestionByCountryName("France");

        // asserções
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("France"));
        assertTrue(response.getBody().toString().contains("Golden Retriever"));
    }

    @Test
    void shouldReturnNotFoundWhenCountryIsNull() {
        //mocks

        DogController dogController = mock(DogController.class);
        CountryController countryController = mock(CountryController.class);
        CountryDogAdapter countryDogAdapter = mock(CountryDogAdapter.class);

        HttpIn httpIn = new HttpIn(dogController, countryController, countryDogAdapter);

        when(countryController.getCountry("Nowhere")).thenReturn(null);

        ResponseEntity<?> response = httpIn.getDogSuggestionByCountryName("Nowhere");

        //fix errors
        assertEquals(404, response.getStatusCode().value());
    }
}