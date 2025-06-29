package com.coelho.desafio.itau.diplomat.wire;

public class CountryWireIn {
    public Name name;
    public String region;
    public int population;
    public Flags flags;

    public static class Name {
        public String common;
    }

    public static class Flags {
        public String png;
    }
}
