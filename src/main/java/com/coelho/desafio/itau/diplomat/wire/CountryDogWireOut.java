package com.coelho.desafio.itau.diplomat.wire;

import com.coelho.desafio.itau.model.Country;
import com.coelho.desafio.itau.model.Dog;
import lombok.Getter;

public record CountryDogWireOut(Country country, Dog dog) {

}
