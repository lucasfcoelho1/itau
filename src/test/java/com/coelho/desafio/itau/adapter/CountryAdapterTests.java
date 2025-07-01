package com.coelho.desafio.itau.adapter;

import com.coelho.desafio.itau.model.Dog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CountryAdapterTests {
    private final DogAdapter adapter = new DogAdapter();

    @Test
    void shouldConvertJsonToDogObject() {
        String json = """
            {
                "name": "Golden Retriever",
                "description": "Amigável, dócil e ótimo para famílias."
            }
        """;

        Dog result = adapter.toModel(json);

        assertNotNull(result);
        assertEquals("Golden Retriever", result.getBreed());
        assertEquals("Amigável, dócil e ótimo para famílias.", result.getDescription());
    }

    @Test
    void shouldReturnNullWhenJsonIsInvalid() {
        String invalidJson = """
            { "nome": "X", "descricao": 123 }
        """;

        Dog result = adapter.toModel(invalidJson);

        assertNull(result);
    }
}
