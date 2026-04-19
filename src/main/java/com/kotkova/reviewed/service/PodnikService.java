package com.kotkova.reviewed.service;


import com.kotkova.reviewed.model.Podnik;
import com.kotkova.reviewed.model.Recenze;
import com.kotkova.reviewed.repository.RecenzeRepository;
import com.kotkova.reviewed.repository.PodnikRepository;
import com.kotkova.reviewed.repository.FotkaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PodnikService {

    private final PodnikRepository podnikRepository;
    private final RecenzeRepository recenzeRepository;
    private final FotkaRepository fotkaRepository;

    // Tímto říkáme Springu, ať nám repozitář "připraví"
    public PodnikService(PodnikRepository podnikRepository, RecenzeRepository recenzeRepository, FotkaRepository fotkaRepository) {
        this.podnikRepository = podnikRepository;
        this.recenzeRepository = recenzeRepository;
        this.fotkaRepository = fotkaRepository;
    }



    // Metoda, která vytáhne z databáze úplně všechny podniky
    public List<Podnik> ziskejVsechnyPodniky() {
        return podnikRepository.findAll();
    }

    public List<Podnik> ziskejNejnovejsiPodniky() {
        List<Podnik> podniky = podnikRepository.findTop3ByOrderByIdPodnikuDesc();

        for (Podnik p : podniky) {
            // 1. Vypočítáme průměr a uložíme ho do transientní proměnné
            p.setPrumernyRating(ziskejPrumernyRating(p.getIdPodniku()));

            // 2. Najdeme titulní fotku (vezmeme první recenzi, která má fotku)
            List<Recenze> recenzePodniku = recenzeRepository.findByPodnikIdPodniku(p.getIdPodniku());
            for (Recenze r : recenzePodniku) {
                List<Long> fotkaIds = fotkaRepository.najdiIdFotekPodleRecenze(r.getIdObsahu());
                if (!fotkaIds.isEmpty()) {
                    p.setIdTitulniFotky(fotkaIds.get(0));
                    break; // Jakmile najdeme jednu fotku, končíme hledání
                }
            }
        }
        return podniky;
    }
    public Podnik ziskejPodnikPodleId(Long id) {
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