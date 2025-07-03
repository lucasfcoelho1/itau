package com.coelho.desafio.itau.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String region;
    private Long totalPopulation;
    private String flagUrl;

    @OneToOne(mappedBy = "country",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Pet pet;

    protected Country() {
    }

    public Country(String title, String region, Long totalPopulation, String flagUrl) {
        this.title = title;
        this.region = region;
        this.totalPopulation = totalPopulation;
        this.flagUrl = flagUrl;
    }

    public Country(String title, String region){
        this.title = title;
        this.region = region;
    }
}
