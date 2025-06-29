package com.coelho.desafio.itau.adapter;

import com.coelho.desafio.itau.diplomat.wire.CountryDogWireOut;
import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Dog;
import org.springframework.stereotype.Service;

@Service
public class CountryDogAdapter {
    public CountryDogWireOut toWire(Dog dog, Country country) {
        return new CountryDogWireOut(country, dog);
    }
}
