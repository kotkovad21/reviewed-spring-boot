package com.kotkova.reviewed.repository;

import com.kotkova.reviewed.model.Podnik;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
// JpaRepository potřebuje vědět, jakou Entitu spravuje (Podnik) a jaký datový typ má její primární klíč (Long)
public interface PodnikRepository extends JpaRepository<Podnik, Long> {
    // Tuto metodu Spring Boot sám pochopí a vygeneruje za tebe správné SQL!
    List<Podnik> findTop3ByOrderByIdPodnikuDesc();
}