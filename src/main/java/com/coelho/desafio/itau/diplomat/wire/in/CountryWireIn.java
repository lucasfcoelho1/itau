package com.coelho.desafio.itau.diplomat.wire.in;

public class CountryWireIn {
    public Name name;
    public String region;
    public Long population;
    public Flags flags;

    public static class Name {
        public String common;
    }

    public static class Flags {
        public String png;
    }
}
