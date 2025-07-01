package com.coelho.desafio.itau.logic;

import com.coelho.desafio.itau.model.Country;
import org.springframework.stereotype.Service;

@Service
public class AiSuggestionLogic {
    public String generatePrompt(Country country) {
        return """
        Me sugira uma raça de cachorro ideal para alguém que vive em um país com as seguintes características:

        Nome do país: %s
        Região: %s
        População total: %d
        Estilo de vida, clima e ambiente devem ser inferidos com base nesses dados.

        Responda apenas com o nome da raça ideal e uma frase explicando o motivo.
                Formato da resposta:
                    {
                      "name": "NomeDaRaça",
                      "description": "Motivo da recomendação"
                    }
                
        Importante: não precisa formatar como json, retorne apenas um texto puro.
        """.formatted(
                country.getTitle(),
                country.getRegion(),
                country.getTotalPopulation()
        );
    }
}
