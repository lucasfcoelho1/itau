package com.coelho.desafio.itau.logic;

import lombok.Getter;

@Getter
public enum CacheKey {
    PET_SUGGESTION("petSuggestion:");;

    private final String displayName;
    CacheKey(String displayName) {
        this.displayName = displayName;
    }
}
