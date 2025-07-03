package com.coelho.desafio.itau.logic;

import com.coelho.desafio.itau.model.Country;
import org.springframework.stereotype.Service;

@Service
public class Logic {
    public String generatePrompt(Country country) {
        return """
                Com base nas informações abaixo sobre um país, recomende uma única raça de cachorro ideal para viver nesse local.
                
                País:
                - Nome: %s
                - Região: %s
                - População total: %d
                
                Responda com um JSON válido contendo duas propriedades:
                - "breed": nome da raça sugerida
                - "description": motivo da recomendação, em uma única frase clara
                
                Exemplo de resposta válida:
                {
                  "breed": "Labrador Retriever",
                  "description": "É amigável e adaptável, ideal para famílias em regiões urbanas de clima ameno."
                }
                
                ⚠️ Importante:
                - NÃO utilize blocos de código ou formatação Markdown como ```json
                - Retorne apenas o JSON cru
                - NÃO adicione explicações antes ou depois
                """.formatted(
                country.getTitle(),
                country.getRegion(),
                country.getTotalPopulation()
        );
    }

    public String buildCacheKey(String countryName) {
        return CacheKey.PET_SUGGESTION.getDisplayName() + countryName.toLowerCase();
    }

}
