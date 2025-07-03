package com.coelho.desafio.itau.service;

import com.coelho.desafio.itau.diplomat.HttpOut;
import com.coelho.desafio.itau.exception.ExternalServiceException;
import com.coelho.desafio.itau.model.Country;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private HttpOut httpOut;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CountryService countryService;

    @Test
    void shouldReturnMappedCountriesFromCache() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("title", "Brazil");
        map.put("region", "Americas");
        map.put("totalPopulation", 200_000_000L);
        map.put("flagUrl", "https://brazil.flag");

        List<?> cachedList = List.of(map);

        List<Country> result = countryService.mappedCachedCountries(cachedList);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Brazil");
    }

    @Test
    void shouldReturnNullWhenCacheIsEmpty() {
        when(cacheService.get("countryList:All", List.class)).thenReturn(null);
        List<?> result = countryService.getCountryListCached();
        assertThat(result).isNull();
    }

    @Test
    void shouldFetchCountriesSuccessfully() {
        var expected = List.of(new Country("Brazil", "Americas"));
        when(httpOut.fetchCountryList()).thenReturn(expected);

        var result = countryService.fetchCountries();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldThrowExceptionWhenHttpOutThrows() {
        when(httpOut.fetchCountryList())
                .thenThrow(new ExternalServiceException("Nenhum país foi encontrado na API externa."));

        assertThrows(ExternalServiceException.class, () -> {
            countryService.fetchCountries();
        });
    }
}