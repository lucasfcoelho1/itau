package com.coelho.desafio.itau.adapter;

import com.coelho.desafio.itau.diplomat.wire.CountryWireIn;
import com.coelho.desafio.itau.model.Country;
import org.springframework.stereotype.Service;

@Service
public class CountryAdapter {

    public Country toModel(CountryWireIn wire){
        return new Country(
                wire.name.common, //name.common->title
                wire.region,
                wire.population, //population->totalPopulation
                wire.flags != null ? wire.flags.png : null
        );
    }
}
