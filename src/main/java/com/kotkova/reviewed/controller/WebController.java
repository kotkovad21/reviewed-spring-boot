package com.kotkova.reviewed.controller;

import com.kotkova.reviewed.model.*;
import com.kotkova.reviewed.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/")
    public String showHomepage(Model model, java.security.Principal principal) { // PŘIDÁNO: Principal
        // 1. Podniky (tohle máš správně)
        var seznamPodniku = podnikService.ziskejNejnovejsiPodniky();
        model.addAttribute("podnikyZDatabaze", seznamPodniku);

        // 2. Zjistíme, kdo je u počítače (idPrihlaseneho)
        Long idPrihlaseneho = null;
        if (principal != null) {
            Uzivatel u = uzivatelService.ziskejUzivatelePodleEmailu(principal.getName());
            if (u != null) idPrihlaseneho = u.getIdUzivatele();
        }

        // 3. TADY JE TA HLAVNÍ ZMĚNA:
        // Místo té staré metody použijeme ziskejStrankuRecenzi s limitem 6
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(0, 6);

        // Tato služba už v sobě má ten chytrý filtr, co jsme psali minule!
        var stranka = recenzeService.ziskejStrankuRecenzi(pageable, idPrihlaseneho);

        // Pošleme do HTML jen těch bezpečných 6 kousků
        model.addAttribute("seznamRecenzi", stranka.getContent());

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
    public String showVisitsPage(Model model, java.security.Principal principal) {

        Pageable pageable = PageRequest.of(0, 12);

        Long idPrihlaseneho = null;
        if (principal != null) {
            Uzivatel u = uzivatelService.ziskejUzivatelePodleEmailu(principal.getName());
            if (u != null) {
                idPrihlaseneho = u.getIdUzivatele();
            }
        }

        var prvniStranka = recenzeService.ziskejMojeRecenze(pageable, idPrihlaseneho);

        // 2. Pošleme je do HTML pod jménem "seznamRecenzi"
        model.addAttribute("seznamRecenzi", prvniStranka.getContent());

        return "visits";
    }

    @GetMapping("/visits/load-more")
    public String loadMore(@RequestParam(defaultValue = "0") int page, Model model, java.security.Principal principal) {
        // 1. Připravíme si stránkování
        Pageable pageable = PageRequest.of(page, 12);

        // 2. Zjistíme ID přihlášeného uživatele (úplně stejně jako v /visits)
        Long idPrihlaseneho = null;
        if (principal != null) {
            Uzivatel u = uzivatelService.ziskejUzivatelePodleEmailu(principal.getName());
            if (u != null) {
                idPrihlaseneho = u.getIdUzivatele();
            }
        }

        // 3. ZAVOLÁME SLUŽBU SE DVĚMA PARAMETRY (tady byla ta chyba)
        Page<Recenze> recenzePage = recenzeService.ziskejMojeRecenze(pageable, idPrihlaseneho);

        // 4. Pošleme data do modelu a vrátíme fragment
        model.addAttribute("seznamRecenzi", recenzePage.getContent());

        return "fragments/visitsLoad :: visits-fragment";
    }

    @GetMapping("/place/{id}")
    public String zobrazDetailPodniku(@PathVariable Long id, Model model, java.security.Principal principal) {
        // 1. Najdeme podnik v DB
        Podnik vybranyPodnik = podnikService.ziskejPodnikPodleId(id);
        model.addAttribute("podnik", vybranyPodnik);

        // 2. Zjistíme ID přihlášeného uživatele (pokud je někdo přihlášen)
        Long idPrihlaseneho = null;
        if (principal != null) {
            Uzivatel u = uzivatelService.ziskejUzivatelePodleEmailu(principal.getName());
            if (u != null) {
                idPrihlaseneho = u.getIdUzivatele();
            }
        }

        // 3. Získáme profiltrované recenze konkrétního podniku (veřejné + přátelé + moje)
        // Tato metoda v RecenzeService zajistí, že nikdo neuvidí, co nemá
        List<Recenze> recenzeKPodniku = recenzeService.ziskejViditelneRecenzeProPodnik(id, idPrihlaseneho);
        model.addAttribute("seznamRecenzi", recenzeKPodniku);

        // 4. Průměrné hodnocení
        Double prumer = podnikService.ziskejPrumernyRating(id);
        model.addAttribute("prumer", prumer != null ? prumer : 0.0);

        return "place";
    }
    @GetMapping("/review/{id}")
    public String showReviewPage(@PathVariable Long id, Model model, java.security.Principal principal) {
        // 1. Najdeme konkrétní recenzi v databázi (to už máš)
        Recenze vybranaRecenze = recenzeService.ziskejRecenziPodleId(id);
        model.addAttribute("recenze", vybranaRecenze);

        // 2. Zjistíme ID přihlášeného uživatele
        // Proměnnou si připravíme nahoře, aby byla viditelná pro model.addAttribute níže
        Long prihlasenyId = null;

        if (principal != null) {
            Uzivatel u = uzivatelService.ziskejUzivatelePodleEmailu(principal.getName());
            if (u != null) {
                prihlasenyId = u.getIdUzivatele();
            }
        }

        // 3. Pošleme ID do HTML pod jménem "prihlasenyId"
        // Teď už proměnná 'u' nesvítí červeně, protože používáme 'prihlasenyId'
        model.addAttribute("prihlasenyId", prihlasenyId);

        // 4. Otevřeme soubor review.html
        return "review";
    }

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

    @PostMapping("/review/{id}/delete")
    @ResponseBody // Říkáme, že nevracíme HTML stránku, ale jen potvrzení (OK)
    public ResponseEntity<String> smazatRecenzi(@PathVariable Long id, java.security.Principal principal) {
        // 1. Získáme přihlášeného uživatele
        Uzivatel prihlaseny = uzivatelService.ziskejUzivatelePodleEmailu(principal.getName());

        // 2. Najdeme recenzi
        Recenze r = recenzeService.ziskejRecenziPodleId(id);

        // 3. BEZPEČNOSTNÍ KONTROLA: Patří ta recenze jemu?
        if (r.getObsah().getUzivatel().getIdUzivatele().equals(prihlaseny.getIdUzivatele())) {
            recenzeService.oznacJakoSmazanou(id);
            return ResponseEntity.ok("Smazáno");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Nemáte oprávnění");
        }
    }

}