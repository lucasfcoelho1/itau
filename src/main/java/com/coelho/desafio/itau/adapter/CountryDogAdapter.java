package com.coelho.desafio.itau.adapter;

import com.coelho.desafio.itau.diplomat.wire.out.CountryDogWireOut;
import com.coelho.desafio.itau.diplomat.wire.out.CountryWireOut;
import com.coelho.desafio.itau.diplomat.wire.out.DogWireOut;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Dog;
import org.springframework.stereotype.Service;

@Service
public class CountryDogAdapter {

    public CountryDogWireOut toWire(Dog dog, Country country) {
        var wireCountry = new CountryWireOut(
                country.getTitle(),
                country.getRegion(),
                country.getTotalPopulation(),
                country.getFlagUrl() != null ? country.getFlagUrl() : null
        );

        var wireDog = new DogWireOut(
                dog.getBreed(),
                dog.getDescription()
        );

        return new CountryDogWireOut(wireCountry, wireDog);
    }
}