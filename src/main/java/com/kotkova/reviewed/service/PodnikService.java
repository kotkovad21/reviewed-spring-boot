package com.kotkova.reviewed.service;


import com.kotkova.reviewed.model.Podnik;
import com.kotkova.reviewed.model.Recenze;
import com.kotkova.reviewed.repository.RecenzeRepository;
import com.kotkova.reviewed.repository.PodnikRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PodnikService {

    private final PodnikRepository podnikRepository;
    private final RecenzeRepository recenzeRepository;

    // Tímto říkáme Springu, ať nám repozitář "připraví"
    public PodnikService(PodnikRepository podnikRepository, RecenzeRepository recenzeRepository) {
        this.podnikRepository = podnikRepository;
        this.recenzeRepository = recenzeRepository;
    }



    // Metoda, která vytáhne z databáze úplně všechny podniky
    public List<Podnik> ziskejVsechnyPodniky() {
        return podnikRepository.findAll();
    }

    // Tuto metodu použijeme teď na Homepage
    public List<Podnik> ziskejNejnovejsiPodniky() {
        return podnikRepository.findTop3ByOrderByIdPodnikuDesc();
    }
    public Podnik ziskejPodnikPodleId(Long id) {
        // findById vrátí "Optional", proto použijeme .orElse(null),
        // aby aplikace nespadla, když podnik s daným ID neexistuje
        return podnikRepository.findById(id).orElse(null);
    }
    public Double ziskejPrumernyRating(Long idPodniku) {
        List<Recenze> recenze = recenzeRepository.findByPodnikIdPodniku(idPodniku);
        if (recenze.isEmpty()) {
            return 0.0;
        }
        double soucet = 0;
        for (Recenze r : recenze) {
            soucet += r.getHodnoceni();
        }
        return soucet / recenze.size();
    }
}