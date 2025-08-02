package com.coopchal.lms.controllers;

import com.coopchal.lms.dtos.CoursDto;
import com.coopchal.lms.models.Cours;
import com.coopchal.lms.enums.Role;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.services.CoursService;
import com.coopchal.lms.services.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cours")
@RequiredArgsConstructor
public class CoursController {

    private final CoursService coursService;
    private final UtilisateurService utilisateurService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<CoursDto> all() {
        return coursService.getAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
    public CoursDto create(Authentication auth, @Valid @RequestBody Cours cours) {
        Long formateurId = utilisateurService.trouverParEmail(auth.getName())
                .orElseThrow().getId();
        return coursService.creerCours(cours, formateurId);
    }

    @PostMapping("/{idCours}/inscrire/{idApprenant}")
    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
    public CoursDto inscrire(@PathVariable Long idCours, @PathVariable Long idApprenant) {
        return coursService.inscrireApprenant(idCours, idApprenant);
    }

    @PostMapping("/{idCours}/inscrire/me")
    @PreAuthorize("hasRole('APPRENANT')")
    public CoursDto selfInscrire(@PathVariable Long idCours, Authentication auth) {
        Long id = utilisateurService.trouverParEmail(auth.getName()).orElseThrow().getId();
        return coursService.inscrireApprenant(idCours, id);
    }

    @GetMapping("/mes-cours-formateur")
    @PreAuthorize("hasRole('FORMATEUR')")
    public List<CoursDto> mesCoursFormateur(Authentication auth) {
        Long id = utilisateurService.trouverParEmail(auth.getName()).orElseThrow().getId();
        return coursService.listerCoursFormateur(id);
    }

    @GetMapping("/mes-cours-apprenant")
    @PreAuthorize("hasRole('APPRENANT')")
    public List<CoursDto> mesCoursApprenant(Authentication auth) {
        Long id = utilisateurService.trouverParEmail(auth.getName()).orElseThrow().getId();
        return coursService.listerCoursApprenant(id);
    }

    // Désinscription par FORMATEUR ou ADMIN uniquement
    @DeleteMapping("/{idCours}/desinscrire/{idApprenant}")
    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
    public CoursDto desinscrire(@PathVariable Long idCours, @PathVariable Long idApprenant, Authentication auth) {
        Utilisateur utilisateurConnecte = utilisateurService.trouverParEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Utilisateur non trouvé"));

        // Si rôle APPRENANT, il ne peut désinscrire que lui-même
        if (utilisateurConnecte.getRole() == Role.APPRENANT && !utilisateurConnecte.getId().equals(idApprenant)) {
            throw new AccessDeniedException("Un apprenant ne peut pas désinscrire un autre apprenant");
        }

        return coursService.desinscrireApprenant(idCours, idApprenant);
    }

    // Désinscription par l'apprenant lui-même
    @DeleteMapping("/{idCours}/desinscrire/me")
    @PreAuthorize("hasRole('APPRENANT')")
    public CoursDto selfDesinscrire(@PathVariable Long idCours, Authentication auth) {
        Long id = utilisateurService.trouverParEmail(auth.getName()).orElseThrow().getId();
        return coursService.desinscrireApprenant(idCours, id);
    }
}
