package com.coelho.desafio.itau.diplomat.wire.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryWireIn {
    public Name name;
    public String region;
    public Long population;
    public Flags flags;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name {
        public String common;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Flags {
        public String png;
    }
}
