package com.kotkova.reviewed.service;
import com.kotkova.reviewed.model.Uzivatel;
import com.kotkova.reviewed.repository.UzivatelRepository;
import org.springframework.stereotype.Service;

@Service
public class UzivatelService {
    private final UzivatelRepository uzivatelRepository;

    public UzivatelService(UzivatelRepository uzivatelRepository) {
        this.uzivatelRepository = uzivatelRepository;
    }

    public Uzivatel ziskejUzivatelePodleId(Long id) {
        return uzivatelRepository.findById(id).orElse(null);
    }
}