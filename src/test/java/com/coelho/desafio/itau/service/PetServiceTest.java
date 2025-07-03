package com.coelho.desafio.itau.service;

import com.coelho.desafio.itau.diplomat.HttpOut;
import com.coelho.desafio.itau.logic.Logic;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Pet;
import com.coelho.desafio.itau.model.PetSuggestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PetServiceTest {

    private CacheService cacheService;
    private Logic logic;
    private HttpOut httpOut;
    private PetService petService;

    @BeforeEach
    void setUp() {
        cacheService = mock(CacheService.class);
        logic = mock(Logic.class);
        httpOut = mock(HttpOut.class);
        petService = new PetService(cacheService, logic, httpOut);
    }

    @Test
    void shouldBuildPetSuggestionFromCountryAndCacheIt() {
        Country country = new Country("Brazil", "Americas");
        String prompt = "Suggest a pet for Brazil";
        Pet pet = new Pet("Jaguar", "Strong and fast");
        when(logic.generatePrompt(country)).thenReturn(prompt);
        when(httpOut.fetchPetSuggestionByCountryPrompt(prompt)).thenReturn(pet);

        PetSuggestion result = petService.buildPetSuggestionFromCountry(country);

        assertThat(result).isNotNull();
        assertThat(result.getCountry()).isEqualTo(country);
        assertThat(result.getPet()).isEqualTo(pet);

        verify(cacheService).set(eq(logic.buildCacheKey(country.getTitle())), eq(result));
    }

    @Test
    void shouldReturnNullIfCountryIsNull() {
        PetSuggestion result = petService.buildPetSuggestionFromCountry(null);

        assertThat(result).isNull();
        verifyNoInteractions(logic, httpOut, cacheService);
    }

    @Test
    void shouldReturnNullIfPromptFails() {
        Country country = new Country("Brazil", "Americas");
        when(logic.generatePrompt(country)).thenReturn(null);

        PetSuggestion result = petService.buildPetSuggestionFromCountry(country);

        assertThat(result).isNull();
        verify(logic).generatePrompt(country);
        verifyNoMoreInteractions(logic, httpOut, cacheService);
    }

    @Test
    void shouldReturnNullIfHttpCallFails() {
        Country country = new Country("Brazil", "Americas");
        String prompt = "Suggest a pet for Brazil";
        when(logic.generatePrompt(country)).thenReturn(prompt);
        when(httpOut.fetchPetSuggestionByCountryPrompt(prompt)).thenReturn(null);

        PetSuggestion result = petService.buildPetSuggestionFromCountry(country);

        assertThat(result).isNull();
        verify(cacheService, never()).set(anyString(), any());
    }
}