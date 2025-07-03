package com.coelho.desafio.itau.logic;

import lombok.Getter;

@Getter
public enum CacheKey {
    PET_SUGGESTION("petSuggestion:"),
    COUNTRY_LIST_ALL("countryList:All");

    private final String displayName;
    CacheKey(String displayName) {
        this.displayName = displayName;
    }
}
