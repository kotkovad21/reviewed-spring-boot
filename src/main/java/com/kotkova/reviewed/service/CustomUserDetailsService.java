package com.kotkova.reviewed.service;

import com.kotkova.reviewed.model.Uzivatel;
import com.kotkova.reviewed.repository.UzivatelRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UzivatelRepository uzivatelRepository;

    public CustomUserDetailsService(UzivatelRepository uzivatelRepository) {
        this.uzivatelRepository = uzivatelRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Najdeme uživatele v databázi podle e-mailu
        Uzivatel mujUzivatel = uzivatelRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Uživatel s emailem " + email + " nebyl nalezen."));

        // 2. Přeložíme ho do formátu, kterému rozumí Spring Security
        return User.builder()
                .username(mujUzivatel.getEmail()) // Jako "jméno" použijeme e-mail
                .password(mujUzivatel.getHeslo())
                .roles("USER") // Zatím dáme všem roli USER, později můžeme napojit tvůj ID_ROLE
                .build();
    }
}