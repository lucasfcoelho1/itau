package com.coelho.desafio.itau.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Country {
    private final String title;
    private final String region;
    private final int totalPopulation;
    private final String flagUrl;

    public Country(String title, String region, int totalPopulation, String flagUrl) {
        this.title = title;
        this.region = region;
        this.totalPopulation = totalPopulation;
        this.flagUrl = flagUrl;
    }
}
