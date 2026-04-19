package com.kotkova.reviewed.repository;

import com.kotkova.reviewed.model.Fotka;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FotkaRepository extends JpaRepository<Fotka, Long> {
    // Metoda, která nám najde všechny fotky k jedné recenzi
    List<Fotka> findByIdRecenze(Long idRecenze);
}