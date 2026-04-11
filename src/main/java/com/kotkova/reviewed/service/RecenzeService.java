package com.kotkova.reviewed.service;

import com.kotkova.reviewed.model.Recenze;
import com.kotkova.reviewed.repository.RecenzeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecenzeService {

    private final RecenzeRepository recenzeRepository;

    // Vložení repozitáře (Dependency Injection)
    public RecenzeService(RecenzeRepository recenzeRepository) {
        this.recenzeRepository = recenzeRepository;
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
        return recenzeRepository.findTop6ByOrderByIdObsahuDesc();
    }
}