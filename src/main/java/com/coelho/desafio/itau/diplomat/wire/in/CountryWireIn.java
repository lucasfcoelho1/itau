package com.coelho.desafio.itau.diplomat.wire.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryWireIn {
    public Name name;
    public String region;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Name {
        public String common;
    }
}
