package com.kotkova.reviewed.model;

import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "UZIVATELE") // Uprav podle svého SQL schématu
public class Uzivatel {

    @Id
    @Column(name = "id_uzivatele")
    private Long idUzivatele;

    @Column(name = "krestni_jmeno")
    private String krestniJmeno;

    @Column(name = "prijmeni")
    private String prijmeni;

    @Column(name = "prezdivka") // Může to být i 'jmeno' nebo 'username'
    private String prezdivka;

    @Column(name = "email")
    private String email;

    @Column(name = "heslo")
    private String heslo;

    @Column(name = "datum_registrace")
    private LocalDate datumRegistrace;

    @Column(name = "popis_profilu")
    private String popisProfilu;

    @ManyToOne
    @JoinColumn(name = "id_mesta")
    private Mesto mesto;

    // --- GETTERY A SETTERY ---
    public Long getIdUzivatele() { return idUzivatele; }
    public void setIdUzivatele(Long idUzivatele) { this.idUzivatele = idUzivatele; }

    public String getKrestniJmeno() { return krestniJmeno; }
    public void setKrestniJmeno(String krestniJmeno) { this.krestniJmeno = krestniJmeno; }

    public String getPrijmeni() { return prijmeni; }
    public void setPrijmeni(String prijmeni) { this.prijmeni = prijmeni; }

    public String getPrezdivka() { return prezdivka; }
    public void setPrezdivka(String prezdivka) { this.prezdivka = prezdivka; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHeslo() { return heslo; }
    public void setHeslo(String heslo) { this.heslo = heslo; }

    public LocalDate getDatumRegistrace() { return datumRegistrace; }
    public void setDatumRegistrace(LocalDate datumRegistrace) { this.datumRegistrace = datumRegistrace; }

    public String getPopisProfilu() { return popisProfilu; }
    public void setPopisProfilu(String popisProfilu) { this.popisProfilu = popisProfilu; }

    public Mesto getMesto() { return mesto; }
    public void setMesto(Mesto mesto) { this.mesto = mesto; }
}