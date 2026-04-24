package com.kotkova.reviewed.service;

import com.kotkova.reviewed.model.Recenze;
import com.kotkova.reviewed.model.Viditelnost;
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
    public Page<Recenze> ziskejStrankuRecenzi(Pageable pageable, Long idPrihlasenehoUzivatele) {
        Page<Recenze> stranka;

        if (idPrihlasenehoUzivatele != null) {
            // Zapínáme chytrý filtr pro přihlášeného!
            stranka = recenzeRepository.najdiViditelneRecenze(idPrihlasenehoUzivatele, pageable);
        } else {
            // Vidí jen veřejné a anonymní věci
            stranka = recenzeRepository.najdiVerejneRecenze(pageable);
        }
        for (Recenze r : stranka.getContent()) {
            pripojIdTitulniFotky(r);
        }
        return stranka;
    }

    public List<Recenze> ziskejViditelneRecenzeProPodnik(Long idPodniku, Long idPrihlasenehoUzivatele) {
        List<Recenze> recenze;

        if (idPrihlasenehoUzivatele != null) {
            // Přihlášený uživatel - zapínáme chytrý filtr
            recenze = recenzeRepository.najdiViditelneRecenzePodniku(idPodniku, idPrihlasenehoUzivatele);
        } else {
            // Nepřihlášený - jen veřejné
            recenze = recenzeRepository.najdiVerejneRecenzePodniku(idPodniku);
        }

        // Pokud zde potřebuješ dotahovat fotky (stejně jako u stránkování), přidej sem ten for-cyklus:
        for (Recenze r : recenze) {
            pripojIdTitulniFotky(r);
        }

        return recenze;
    }

    private void pripojIdTitulniFotky(Recenze r) {
        List<Long> ids = fotkaRepository.najdiIdFotekPodleRecenze(r.getIdObsahu());
        if (!ids.isEmpty()) {
            r.setIdTitulniFotky(ids.get(0));
        }
    }

    // PŘIDEJ TUTO NOVOU METODU DO RecenzeService
    public Page<Recenze> ziskejMojeRecenze(Pageable pageable, Long idPrihlasenehoUzivatele) {

        // Získáme z databáze POUZE recenze aktuálního uživatele
        Page<Recenze> stranka = recenzeRepository.findByObsahUzivatelIdUzivateleOrderByIdObsahuDesc(idPrihlasenehoUzivatele, pageable);

        // Kód pro bleskové načítání fotek z minula
        for (Recenze r : stranka.getContent()) {
            pripojIdTitulniFotky(r);
        }

        return stranka;
    }

    public void oznacJakoSmazanou(Long idRecenze) {
        Recenze r = recenzeRepository.findById(idRecenze).orElse(null);
        if (r != null) {

            // 1. Vytvoříme si nový (pomocný) objekt Viditelnost
            Viditelnost smazanaViditelnost = new Viditelnost();

            // 2. Nastavíme mu ID 5 (Smazaný)
            smazanaViditelnost.setIdViditelnosti(5L);;

            // 3. TADY JE HLAVNÍ ZMĚNA: Recenzi nepředěláváme starou viditelnost,
            // ale rovnou jí "nasadíme" tento náš nový objekt.
            r.getObsah().setViditelnost(smazanaViditelnost);

            // Uložíme do databáze
            recenzeRepository.save(r);
        }
    }

    public Page<Recenze> ziskejFiltrovaneMojeRecenze(Pageable pageable, Long idUzivatele, String business, List<String> tags) {

        // 1. Převod Stringu "all" nebo "1" na Long pro databázi
        Long typId = (business == null || business.equals("all")) ? null : Long.parseLong(business);

        // 2. Počet štítků (potřebujeme pro databázi, pokud hledáme shodu všech)
        Long tagCount = (tags == null || tags.isEmpty()) ? null : (long) tags.size();

        // 3. Zavolání našeho nového filtru z Repository
        Page<Recenze> stranka = recenzeRepository.najdiMojeFiltrovaneRecenze(idUzivatele, typId, tags, tagCount, pageable);

        // 4. Bleskové dotažení titulních fotek (tvá původní logika)
        for (Recenze r : stranka.getContent()) {
            pripojIdTitulniFotky(r);
        }

        return stranka;
    }
}