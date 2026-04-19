package com.kotkova.reviewed.service;

import com.kotkova.reviewed.model.Recenze;
import com.kotkova.reviewed.repository.FotkaRepository;
import com.kotkova.reviewed.repository.RecenzeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecenzeService {

    private final RecenzeRepository recenzeRepository;
    private final FotkaRepository fotkaRepository;

    // Vložení repozitáře (Dependency Injection)
    public RecenzeService(RecenzeRepository recenzeRepository, FotkaRepository fotkaRepository) {
        this.recenzeRepository = recenzeRepository;
        this.fotkaRepository = fotkaRepository;
    }

    // Tuto metodu použijeme, až budeme odesílat formulář s novou recenzí z HTML
    public Recenze ulozRecenzi(Recenze recenze) {
        return recenzeRepository.save(recenze);
    }

    // Metoda pro získání jedné recenze (kdybychom ji někdy potřebovali např. smazat)
    public Recenze ziskejRecenziPodleId(Long id) {
        return recenzeRepository.findById(id).orElse(null);
    }
    // Tuto starou metodu si tam nech
    public List<Recenze> ziskejVsechnyRecenze() {
        return recenzeRepository.findAll();
    }

    // A TUTO NOVOU PŘIDEJ PRO HOMEPAGE:
    public List<Recenze> ziskejSestNejnovejsichRecenzi() {
        List<Recenze> seznam = recenzeRepository.findTop6ByOrderByIdObsahuDesc();
        for (Recenze r : seznam) {
            pripojIdTitulniFotky(r);
        }
        return seznam;
    }
    public Page<Recenze> ziskejStrankuRecenzi(Pageable pageable) {
        Page<Recenze> stranka = recenzeRepository.findAll(pageable);
        for (Recenze r : stranka.getContent()) {
            pripojIdTitulniFotky(r);
        }
        return stranka;
    }

    private void pripojIdTitulniFotky(Recenze r) {
        List<Long> ids = fotkaRepository.najdiIdFotekPodleRecenze(r.getIdObsahu());
        if (!ids.isEmpty()) {
            r.setIdTitulniFotky(ids.get(0));
        }
    }
}