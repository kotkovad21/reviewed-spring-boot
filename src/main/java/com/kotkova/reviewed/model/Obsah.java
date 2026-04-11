package com.kotkova.reviewed.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "OBSAHY")
public class Obsah {

    @Id
    @Column(name = "id_obsahu")
    private Long idObsahu;

    // @Lob říká Springu, že v databázi je to velký text (CLOB)
    @Lob
    @Column(name = "text")
    private String text;

    @Column(name = "datum_vytvoreni")
    private LocalDate datumVytvoreni;

    // Přidej toto k ostatním proměnným v Obsah.java
    @ManyToOne
    @JoinColumn(name = "id_uzivatele") // Sloupeček v tabulce OBSAHY
    private Uzivatel uzivatel;
    @ManyToMany
    @JoinTable(
            name = "OBSAHY_STITKY", // Název vazební tabulky v DB
            joinColumns = @JoinColumn(name = "id_obsahu"),
            inverseJoinColumns = @JoinColumn(name = "id_stitku")
    )
    private List<Stitek> stitky;

    // Nezapomeň dolů přidat Getter a Setter!
    public List<Stitek> getStitky() { return stitky; }
    public void setStitky(List<Stitek> stitky) { this.stitky = stitky; }

    public Uzivatel getUzivatel() { return uzivatel; }
    public void setUzivatel(Uzivatel uzivatel) { this.uzivatel = uzivatel; }

    public Long getIdObsahu() { return idObsahu; }
    public void setIdObsahu(Long idObsahu) { this.idObsahu = idObsahu; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public LocalDate getDatumVytvoreni() { return datumVytvoreni; }
    public void setDatumVytvoreni(LocalDate datumVytvoreni) { this.datumVytvoreni = datumVytvoreni; }
}