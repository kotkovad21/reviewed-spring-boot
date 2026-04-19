package com.kotkova.reviewed.repository;

import com.kotkova.reviewed.model.Recenze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecenzeRepository extends JpaRepository<Recenze, Long> {
    List<Recenze> findTop6ByOrderByIdObsahuDesc();
    List<Recenze> findByPodnikIdPodniku(Long idPodniku);

}
