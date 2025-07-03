package com.coelho.desafio.itau.diplomat.wire.out;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CountryWireOut {
    private String name;
    private String region;
}