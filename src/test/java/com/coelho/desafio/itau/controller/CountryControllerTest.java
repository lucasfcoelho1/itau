package com.coelho.desafio.itau.controller;

import com.coelho.desafio.itau.logic.CacheKey;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.service.CacheService;
import com.coelho.desafio.itau.service.CountryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CountryControllerTest {

    private CountryService countryService;
    private CacheService cacheService;
    private CountryController countryController;

    @BeforeEach
    void setUp() {
        countryService = mock(CountryService.class);
        cacheService = mock(CacheService.class);
        countryController = new CountryController(countryService, cacheService);
    }

    @Test
    void shouldReturnCountryByName() {
        // Given
        var expectedCountry = new Country("Brazil", "Americas");
        when(countryService.fetchCountryByName("brazil")).thenReturn(expectedCountry);

        // When
        var result = countryController.getCountry("brazil");

        // Then
        assertThat(result).isEqualTo(expectedCountry);
        verify(countryService).fetchCountryByName("brazil");
    }

    @Test
    void shouldReturnFilteredCountriesFromCache() {
        // Given
        var map1 = new LinkedHashMap<String, String>();
        map1.put("title", "Brazil");
        map1.put("region", "Americas");

        var map2 = new LinkedHashMap<String, String>();
        map2.put("title", "Germany");
        map2.put("region", "Europe");

        @SuppressWarnings("unchecked")
        List rawCache = List.of(map1, map2);

        var mapped = List.of(
                new Country("Brazil", "Americas"),
                new Country("Germany", "Europe")
        );

        when(countryService.getCountryListCached()).thenReturn(rawCache);
        when(countryService.mappedCachedCountries(rawCache)).thenReturn(mapped);

        // When
        var result = countryController.getCountries("braz", "americas");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Brazil");
        verify(countryService).getCountryListCached();
        verify(countryService).mappedCachedCountries(rawCache);
        verifyNoInteractions(cacheService);
    }

    @Test
    void shouldReturnFetchedCountriesWhenCacheIsEmpty() {
        // Given
        when(countryService.getCountryListCached()).thenReturn(null);

        var fetched = List.of(
                new Country("Brazil", "Americas"),
                new Country("Germany", "Europe")
        );

        when(countryService.fetchCountries()).thenReturn(fetched);

        // When
        var result = countryController.getCountries(null, null);

        // Then
        assertThat(result).containsExactlyElementsOf(fetched);
        verify(countryService).fetchCountries();
        verify(cacheService).set(CacheKey.COUNTRY_LIST_ALL.getDisplayName(), fetched);
    }
}