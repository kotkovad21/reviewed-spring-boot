package com.kotkova.reviewed.repository;

import com.kotkova.reviewed.model.Recenze;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecenzeRepository extends JpaRepository<Recenze, Long> {
    List<Recenze> findTop6ByOrderByIdObsahuDesc();
    List<Recenze> findByPodnikIdPodniku(Long idPodniku);

    @Query("SELECT r FROM Recenze r WHERE r.obsah.viditelnost.idViditelnosti != 5 AND (" +
            "r.obsah.viditelnost.idViditelnosti IN (1, 2) " + // 1=Anonymní, 2=Veřejné
            "OR r.obsah.uzivatel.idUzivatele = :mojeId " + // Moje vlastní (vidím i 4=Pouze já)
            "OR (r.obsah.viditelnost.idViditelnosti = 3 AND EXISTS (" + // 3=Pouze přátelé
            "    SELECT p FROM Pratelstvi p WHERE p.stav.idStavu = 2 AND (" + // Kde je přátelství 2=Přijato
            "        (p.zadatel.idUzivatele = :mojeId AND p.prijemce.idUzivatele = r.obsah.uzivatel.idUzivatele) OR " +
            "        (p.prijemce.idUzivatele = :mojeId AND p.zadatel.idUzivatele = r.obsah.uzivatel.idUzivatele)" +
            "    )" +
            "))) ORDER BY r.idObsahu DESC")
    Page<Recenze> najdiViditelneRecenze(@Param("mojeId") Long mojeId, Pageable pageable);

    // NOVÉ: Filtr pro NEPŘIHLÁŠENÉHO návštěvníka
    @Query("SELECT r FROM Recenze r WHERE r.obsah.viditelnost.idViditelnosti IN (1, 2) ORDER BY r.idObsahu DESC")
    Page<Recenze> najdiVerejneRecenze(Pageable pageable);

    // Najde jen recenze konkrétního uživatele a seřadí je od nejnovější
    Page<Recenze> findByObsahUzivatelIdUzivateleOrderByIdObsahuDesc(Long idUzivatele, Pageable pageable);

    // NOVÉ: Facebook filtr pro KONKRÉTNÍ PODNIK (pro přihlášené)
    @Query("SELECT r FROM Recenze r WHERE r.podnik.idPodniku = :idPodniku AND r.obsah.viditelnost.idViditelnosti != 5 AND (" +
            "r.obsah.viditelnost.idViditelnosti IN (1, 2) " +
            "OR r.obsah.uzivatel.idUzivatele = :mojeId " +
            "OR (r.obsah.viditelnost.idViditelnosti = 3 AND EXISTS (" +
            "    SELECT p FROM Pratelstvi p WHERE p.stav.idStavu = 2 AND (" +
            "        (p.zadatel.idUzivatele = :mojeId AND p.prijemce.idUzivatele = r.obsah.uzivatel.idUzivatele) OR " +
            "        (p.prijemce.idUzivatele = :mojeId AND p.zadatel.idUzivatele = r.obsah.uzivatel.idUzivatele)" +
            "    )" +
            "))) ORDER BY r.idObsahu DESC")
    List<Recenze> najdiViditelneRecenzePodniku(@Param("idPodniku") Long idPodniku, @Param("mojeId") Long mojeId);

    // NOVÉ: Filtr pro KONKRÉTNÍ PODNIK (pro nepřihlášené)
    @Query("SELECT r FROM Recenze r WHERE r.podnik.idPodniku = :idPodniku AND r.obsah.viditelnost.idViditelnosti IN (1, 2) ORDER BY r.idObsahu DESC")
    List<Recenze> najdiVerejneRecenzePodniku(@Param("idPodniku") Long idPodniku);

    @Query("SELECT r FROM Recenze r " +
            "WHERE r.obsah.uzivatel.idUzivatele = :mojeId " +
            "AND r.obsah.viditelnost.idViditelnosti != 5 " +
            "AND (:typId IS NULL OR r.podnik.typPodniku.idTypuPodniku = :typId) " +
            "AND (:tagCount IS NULL OR r.idObsahu IN (" +
            "    SELECT r2.idObsahu FROM Recenze r2 " +
            "    JOIN r2.podnik.stitky s2 " +
            "    WHERE s2.nazev IN :tags " +
            "    GROUP BY r2.idObsahu " +
            "    HAVING COUNT(DISTINCT s2.idStitku) >= :tagCount" +
            "))")
    Page<Recenze> najdiMojeFiltrovaneRecenze(
            @Param("mojeId") Long mojeId,
            @Param("typId") Long typId,
            @Param("tags") List<String> tags,
            @Param("tagCount") Long tagCount,
            Pageable pageable);
}
