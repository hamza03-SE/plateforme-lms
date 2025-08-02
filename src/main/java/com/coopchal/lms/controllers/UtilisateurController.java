package com.coopchal.lms.controllers;

import com.coopchal.lms.dtos.UtilisateurUpdateDto;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Utilisateur> findAll() {
        return utilisateurService.listerTous();
    }

    @GetMapping("/me")
    public Utilisateur me(Authentication auth) {
        return utilisateurService.trouverParEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));
    }

    @PatchMapping("/me")
    public Utilisateur updateMe(Authentication auth, @RequestBody UtilisateurUpdateDto updatedDto) {
        Utilisateur me = utilisateurService.trouverParEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable"));

        if (updatedDto.getNom() != null) {
            me.setNom(updatedDto.getNom());
        }
        if (updatedDto.getPrenom() != null) {
            me.setPrenom(updatedDto.getPrenom());
        }
        if (updatedDto.getMotDePasse() != null) {
            me.setMotDePasse(new BCryptPasswordEncoder().encode(updatedDto.getMotDePasse()));
        }

        return utilisateurService.save(me);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        utilisateurService.supprimer(id);
    }
}
