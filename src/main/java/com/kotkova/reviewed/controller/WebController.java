package com.kotkova.reviewed.controller;

import com.kotkova.reviewed.model.*;
import com.kotkova.reviewed.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.multipart.MultipartFile;

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
    private final FotkaService fotkaService;

    public WebController(PodnikService podnikService, RecenzeService recenzeService, UzivatelService uzivatelService, TypPodnikuService typPodnikuService, ViditelnostService viditelnostService, StitekService stitekService, FotkaService fotkaService) {

        this.podnikService = podnikService;
        this.recenzeService = recenzeService;
        this.uzivatelService = uzivatelService;
        this.typPodnikuService = typPodnikuService;
        this.viditelnostService = viditelnostService;
        this.stitekService = stitekService;
        this.fotkaService = fotkaService;
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
    public String showProfilePage(Model model, java.security.Principal principal) {
        String email = principal.getName();

        Uzivatel prihlasenyUzivatel = uzivatelService.ziskejUzivatelePodleEmailu(email);
        model.addAttribute("uzivatel", prihlasenyUzivatel);

        return "profile";
    }
    @GetMapping("/visits")
    public String showVisitsPage(Model model) {

        Pageable pageable = PageRequest.of(0, 12);
        var prvniStranka = recenzeService.ziskejStrankuRecenzi(pageable);

        // 2. Pošleme je do HTML pod jménem "seznamRecenzi"
        model.addAttribute("seznamRecenzi", prvniStranka.getContent());

        return "visits";
    }

    @GetMapping("/visits/load-more")
    public String loadMore(@RequestParam(defaultValue = "0") int page, Model model) {
        // 1. Připravíme si stránkování (velikost 12)
        Pageable pageable = PageRequest.of(page, 12);

        // 2. Místo repository voláme SERVICE (teď už to nebude svítit červeně)
        Page<Recenze> recenzePage = recenzeService.ziskejStrankuRecenzi(pageable);

        // 3. Pošleme data do modelu
        model.addAttribute("seznamRecenzi", recenzePage.getContent());

        // 4. Vrátíme fragment
        return "fragments/visitsLoad :: visits-fragment";
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

        Double prumer = podnikService.ziskejPrumernyRating(id);
        model.addAttribute("prumer", prumer != null ? prumer : 0.0);
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
    public String processNewReview(
            @ModelAttribute("novaRecenze") Recenze recenze,
            @RequestParam("fotkySoubory") MultipartFile[] soubory,
                java.security.Principal principal){
        try {
            // 1. Nastavíme autora a základní info (tvoje stávající logika)
            String email = principal.getName();
            Uzivatel autor = uzivatelService.ziskejUzivatelePodleEmailu(email);
            recenze.getObsah().setUzivatel(autor);
            recenze.getObsah().setDatumVytvoreni(LocalDate.now());
            recenze.getObsah().setTypObsahu("RECENZE");

            if (recenze.getObsah().getViditelnost() == null) {
                Viditelnost defaultViditelnost = viditelnostService.ziskejVsechnyViditelnosti().get(0);
                recenze.getObsah().setViditelnost(defaultViditelnost);
            }

            recenze.getObsah().setRecenze(recenze);

            // 2. Uložíme recenzi (tím získáme ID_OBSAHU, které potřebujeme pro fotky)
            Recenze ulozena = recenzeService.ulozRecenzi(recenze);

            // 3. Zpracování nahraných fotek
            if (soubory != null) {
                for (MultipartFile soubor : soubory) {
                    if (!soubor.isEmpty()) {
                        Fotka novaFotka = new Fotka();
                        novaFotka.setData(soubor.getBytes()); // Převede soubor na byte[] (BLOB)
                        novaFotka.setIdRecenze(ulozena.getIdObsahu()); // Propojí fotku s recenzí

                        novaFotka.setNazevSouboru(soubor.getOriginalFilename());
                        // Uložíme fotku (předpokládám, že máš fotkaService nebo fotkaRepository)

                        String unikatniNazev = java.util.UUID.randomUUID().toString().substring(0, 20);
                        novaFotka.setNazevSouboru(unikatniNazev);

                        fotkaService.ulozFotku(novaFotka);
                    }
                }
            }

            return "redirect:/visits";
        } catch (Exception e) {
            e.printStackTrace();
            return "insert";
        }
    }
    // PŘIDAT DO WebControlleru
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Zobrazí templates/login.html
    }
}