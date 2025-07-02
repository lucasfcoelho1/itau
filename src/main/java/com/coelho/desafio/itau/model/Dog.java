package com.coelho.desafio.itau.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String breed;
    private String description;

    @OneToOne
    @JoinColumn(name = "country_id", referencedColumnName = "id")
    private Country country;

    public Dog() {
    }

    @JsonCreator
    public Dog(
            @JsonProperty("breed") String breed,
            @JsonProperty("description") String description) {
        this.breed = breed;
        this.description = description;
    }
}