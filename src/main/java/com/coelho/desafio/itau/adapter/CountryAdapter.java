package com.coelho.desafio.itau.adapter;

import com.coelho.desafio.itau.adapter.wire.CountryWireIn;
import com.coelho.desafio.itau.model.Country;
import org.springframework.stereotype.Service;

@Service
public class CountryAdapter {

    public Country mapToCountry(CountryWireIn wire){
        return new Country(
                wire.name.common, //name->title
                wire.region,
                wire.population, //population->totalPopulation
                wire.flags != null ? wire.flags.png : null
        );
    }
}
