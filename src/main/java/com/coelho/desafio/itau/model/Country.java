package com.coelho.desafio.itau.model;

import lombok.Getter;
import lombok.Setter;

public record Country(String title, String region, int totalPopulation, String flagUrl) {
}
