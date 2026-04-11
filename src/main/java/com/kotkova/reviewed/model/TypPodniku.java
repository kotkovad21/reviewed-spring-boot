package com.kotkova.reviewed.model;
import jakarta.persistence.*;
@Entity
@Table(name = "TYPY_PODNIKU")
public class TypPodniku {

    @Id
    @Column(name = "id_typu_podniku")
    private Long idTypuPodniku;

    @Column(name = "nazev")
    private String nazev;

    // --- GETTERY A SETTERY ---
    public Long getIdTypuPodniku() { return idTypuPodniku; }
    public void setIdTypuPodniku(Long idTypuPodniku) { this.idTypuPodniku = idTypuPodniku; }

    public String getNazev() { return nazev; }
    public void setNazev(String nazev) { this.nazev = nazev; }
}