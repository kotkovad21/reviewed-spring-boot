package com.kotkova.reviewed.controller;

import com.kotkova.reviewed.model.Fotka;
import com.kotkova.reviewed.repository.FotkaRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class FotkaController {

    private final FotkaRepository fotkaRepository;

    public FotkaController(FotkaRepository fotkaRepository) {
        this.fotkaRepository = fotkaRepository;
    }

    // Tento endpoint vrací obrázek místo HTML stránky
    @GetMapping("/image/fotka/{id}")
    public ResponseEntity<byte[]> ziskajObrazok(@PathVariable Long id) {
        Fotka fotka = fotkaRepository.findById(id).orElse(null);

        if (fotka != null && fotka.getData() != null) {
            return ResponseEntity.ok()
                    // Řekneme prohlížeči, že posíláme obrázek (JPEG/PNG)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(fotka.getData());
        }
        return ResponseEntity.notFound().build();
    }
}