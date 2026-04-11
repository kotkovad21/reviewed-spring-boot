package com.kotkova.reviewed.model;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "RECENZE") // Musí přesně odpovídat názvu tabulky v Oracle
public class Recenze {

    @Id // Označuje primární klíč
    @Column(name = "id_obsahu")
    private Long idObsahu;

    @Column(name = "hodnoceni")
    private Integer hodnoceni;

    @Column(name = "datum_navstevy")
    private LocalDate datumNavstevy;

    @Column(name = "utrata")
    private Double utrata;

    // @ManyToOne znamená: "Více různých podniků může mít jeden stejný typ (např. vícero kaváren)"
    @ManyToOne
    // @JoinColumn říká Springu, podle jakého sloupečku to má v databázi spojit
    @JoinColumn(name = "id_podniku")
    private Podnik podnik;

    @OneToOne
    @PrimaryKeyJoinColumn // Spojení proběhne tak, že id_obsahu v RECENZE = id_obsahu v OBSAHY
    private Obsah obsah;

    public Long getIdObsahu() { return idObsahu; }
    public void setIdObsahu(Long idObsahu) {this.idObsahu = idObsahu; }

    public Podnik getPodnik() { return podnik; }
    public void setPodnik(Podnik podnik) { this.podnik = podnik; }

    public Integer getHodnoceni() { return hodnoceni; }
    public void setHodnoceni(Integer hodnoceni) { this.hodnoceni = hodnoceni; }

    public LocalDate getDatumNavstevy() { return datumNavstevy; }
    public void setDatumNavstevy(LocalDate datumNavstevy) { this.datumNavstevy = datumNavstevy; }

    public double getUtrata() { return utrata; }
    public void setUtrata(double utrata) { this.utrata = utrata; }

    public Obsah getObsah() { return obsah; }
    public void setObsah(Obsah obsah) { this.obsah = obsah; }
}