package com.coelho.desafio.itau.repository;

import com.coelho.desafio.itau.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByTitleIgnoreCase(String title);
}
