package com.coelho.desafio.itau.controller;

import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Pet;
import com.coelho.desafio.itau.model.PetSuggestion;
import com.coelho.desafio.itau.service.PetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

class PetControllerTest {

    private PetService petService;
    private CountryController countryController;
    private PetController petController;

    @BeforeEach
    void setUp() {
        petService = mock(PetService.class);
        countryController = mock(CountryController.class);
        petController = new PetController(petService, countryController);
    }

    @Test
    void shouldReturnPetSuggestionFromCache_WhenCacheExists() {
        // Given
        String countryName = "brazil";
        Country mockCountry = new Country("Brazil", "Americas");
        Pet mockPet = new Pet("Jaguar", "Carnivore");

        PetSuggestion cachedSuggestion = new PetSuggestion(mockCountry, mockPet);

        when(petService.getPetSuggestionCached(countryName)).thenReturn(cachedSuggestion);

        // When
        PetSuggestion result = petController.generatePetSuggestionByCountryName(countryName);

        // Then
        assertThat(result).isEqualTo(cachedSuggestion);
        verify(petService).getPetSuggestionCached(countryName);
        verifyNoInteractions(countryController);
        verify(petService, never()).buildPetSuggestionFromCountry(any());
    }

    @Test
    void shouldBuildPetSuggestion_WhenCacheIsEmpty() {
        // Given
        String countryName = "japan";
        when(petService.getPetSuggestionCached(countryName)).thenReturn(null);

        Country country = new Country("Japan", "Asia");
        Pet pet = new Pet("Shiba Inu", "Omnivore");
        PetSuggestion suggestion = new PetSuggestion(country, pet);

        when(countryController.getCountry(countryName)).thenReturn(country);
        when(petService.buildPetSuggestionFromCountry(country)).thenReturn(suggestion);

        // When
        PetSuggestion result = petController.generatePetSuggestionByCountryName(countryName);

        // Then
        assertThat(result).isEqualTo(suggestion);
        verify(petService).getPetSuggestionCached(countryName);
        verify(countryController).getCountry(countryName);
        verify(petService).buildPetSuggestionFromCountry(country);
    }
}