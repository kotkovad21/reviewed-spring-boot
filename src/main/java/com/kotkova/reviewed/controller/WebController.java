package com.kotkova.reviewed.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.kotkova.reviewed.service.PodnikService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kotkova.reviewed.model.Podnik;
import com.kotkova.reviewed.service.RecenzeService; // Nezapomeň import
import com.kotkova.reviewed.model.Recenze; // Nezapomeň import
import com.kotkova.reviewed.model.Uzivatel;
import com.kotkova.reviewed.service.UzivatelService;
import com.kotkova.reviewed.model.TypPodniku;
import com.kotkova.reviewed.service.TypPodnikuService;
import com.kotkova.reviewed.model.Viditelnost;
import com.kotkova.reviewed.service.ViditelnostService;
import com.kotkova.reviewed.model.Stitek;
import com.kotkova.reviewed.service.StitekService;

import java.time.LocalDate;
import java.util.List;

@Controller
public class WebController {

    private final PodnikService podnikService;
    private final RecenzeService recenzeService;
    private final UzivatelService uzivatelService;
    private final TypPodnikuService typPodnikuService;
    private final ViditelnostService viditelnostService;
    private final StitekService stitekService;

    public WebController(PodnikService podnikService, RecenzeService recenzeService, UzivatelService uzivatelService, TypPodnikuService typPodnikuService, ViditelnostService viditelnostService, StitekService stitekService) {

        this.podnikService = podnikService;
        this.recenzeService = recenzeService;
        this.uzivatelService = uzivatelService;
        this.typPodnikuService = typPodnikuService;
        this.viditelnostService = viditelnostService;
        this.stitekService = stitekService;
    }

    // Tato metoda zachytí požadavek, když půjdeš na hlavní stránku ("/")
    @GetMapping("/")
    public String showHomepage(Model model) {
        // Vytáhneme podniky z Oracle
        var seznamPodniku = podnikService.ziskejNejnovejsiPodniky();

        // Pošleme je do HTML pod jménem "podnikyZDatabaze"
        model.addAttribute("podnikyZDatabaze", seznamPodniku);

        var seznamRecenzi = recenzeService.ziskejSestNejnovejsichRecenzi();
        model.addAttribute("seznamRecenzi", seznamRecenzi);
        return "homepage";
    }
    @GetMapping("/place")
    public String showPlacePage() {
        return "place"; // Toto hledá soubor src/main/resources/templates/place.html
    }

    @GetMapping("/insert")
    public String showInsertForm(Model model) {
        // 1. Načteme data ze všech služeb
        List<Podnik> podniky = podnikService.ziskejVsechnyPodniky();
        List<TypPodniku> typy = typPodnikuService.ziskejVsechnyTypy();
        List<Viditelnost> viditelnosti = viditelnostService.ziskejVsechnyViditelnosti();

        model.addAttribute("podniky", podniky);
        model.addAttribute("typyPodniku", typy);
        model.addAttribute("viditelnosti", viditelnosti);
        model.addAttribute("vsechnyStitky", stitekService.ziskejVsechnyStitky());
        model.addAttribute("novaRecenze", new Recenze());
        return "insert";
    }
    @GetMapping("/profile")
    public String showProfilePage(Model model) {
        Uzivatel prihlasenyUzivatel = uzivatelService.ziskejUzivatelePodleId(3L);

        model.addAttribute("uzivatel", prihlasenyUzivatel);

        return "profile";
    }
    @GetMapping("/visits")
    public String showVisitsPage(Model model) {
        // 1. Vytáhneme z databáze všechny recenze
        // (V budoucnu, až budeme mít přihlašování, tady vytáhneme jen recenze konkrétního uživatele)
        var vsechnyRecenze = recenzeService.ziskejVsechnyRecenze();

        // 2. Pošleme je do HTML pod jménem "seznamRecenzi"
        model.addAttribute("seznamRecenzi", vsechnyRecenze);

        return "visits";
    }

    @GetMapping("/place/{id}")
    public String zobrazDetailPodniku(@PathVariable Long id, Model model) {
        // 1. Najdeme podnik v DB
        Podnik vybranyPodnik = podnikService.ziskejPodnikPodleId(id);

        // 2. Pokud neexistuje, pošleme uživatele zpět na homepage
        if (vybranyPodnik == null) {
            return "redirect:/";
        }

        // 3. Pošleme nalezený objekt do HTML pod jménem "podnik"
        model.addAttribute("podnik", vybranyPodnik);

        return "place";
    }
    @GetMapping("/review/{id}")
    public String showReviewPage(@PathVariable Long id, Model model) {
        // 1. Najdeme konkrétní recenzi v databázi
        Recenze vybranaRecenze = recenzeService.ziskejRecenziPodleId(id);

        // 2. Pokud někdo zadal vymyšlené ID, pošleme ho domů
        if (vybranaRecenze == null) {
            return "redirect:/";
        }

        // 3. Pošleme recenzi do HTML pod jménem "recenze"
        model.addAttribute("recenze", vybranaRecenze);

        // 4. Toto říká Springu: "Otevři soubor review.html"
        return "review"; }

    @PostMapping("/insert")
    public String processNewReview(@ModelAttribute("novaRecenze") Recenze recenze) {
        try {
            // 1. Nastavíme autora (ID 3)
            Uzivatel autor = uzivatelService.ziskejUzivatelePodleId(3L);
            recenze.getObsah().setUzivatel(autor);
            recenze.getObsah().setDatumVytvoreni(LocalDate.now());

            recenze.getObsah().setTypObsahu("RECENZE");
            // 2. POJIŠTĚNÍ PRO VIDITELNOST (pokud select neposlal data)
            if (recenze.getObsah().getViditelnost() == null) {
                // Zkusíme najít první viditelnost v DB (obvykle ID 1 je 'Veřejné')
                Viditelnost defaultViditelnost = viditelnostService.ziskejVsechnyViditelnosti().get(0);
                recenze.getObsah().setViditelnost(defaultViditelnost);
            }

            recenze.getObsah().setRecenze(recenze);
            recenzeService.ulozRecenzi(recenze);

            return "redirect:/visits";
        } catch (Exception e) {
            e.printStackTrace();
            return "insert";
        }
    }
}