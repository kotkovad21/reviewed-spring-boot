package com.kotkova.reviewed.repository;

import com.kotkova.reviewed.model.Fotka;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FotkaRepository extends JpaRepository<Fotka, Long> {
    // Metoda, která nám najde všechny fotky k jedné recenzi
    List<Fotka> findByIdRecenze(Long idRecenze);

    @Query("SELECT f.idFotky FROM Fotka f WHERE f.idRecenze = :idRecenze")
    List<Long> najdiIdFotekPodleRecenze(@Param("idRecenze") Long idRecenze);
}