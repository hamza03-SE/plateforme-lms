package com.coopchal.lms.controllers;


import com.coopchal.lms.dtos.InscriptionDto;
import com.coopchal.lms.enums.Role;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.services.InscriptionService;
import com.coopchal.lms.services.UtilisateurService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inscriptions")
@RequiredArgsConstructor
public class InscriptionController {

    private final InscriptionService inscriptionService;
    private final UtilisateurService utilisateurService;

    // 🔽 Inscrire un apprenant à un cours (admin ou formateur)
    @PostMapping("/cours/{idCours}/apprenant/{idApprenant}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR')")
    public InscriptionDto inscrire(@PathVariable Long idCours, @PathVariable Long idApprenant) {
        return inscriptionService.inscrire(idCours, idApprenant);
    }

    // 🔽 Auto-inscription par l’apprenant connecté
    @PostMapping("/cours/{idCours}/me")
    @PreAuthorize("hasRole('APPRENANT')")
    public InscriptionDto selfInscrire(@PathVariable Long idCours, Authentication auth) {
        Long id = utilisateurService.trouverParEmail(auth.getName())
                .orElseThrow().getId();
        return inscriptionService.inscrire(idCours, id);
    }

    // 🔽 Désinscription par formateur/admin
    @DeleteMapping("/cours/{idCours}/apprenant/{idApprenant}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FORMATEUR', 'APPRENANT')")
    public InscriptionDto desinscrire(@PathVariable Long idCours,
                                      @PathVariable Long idApprenant,
                                      Authentication auth) {

        Utilisateur utilisateur = utilisateurService.trouverParEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Utilisateur non trouvé"));

        // Un apprenant ne peut se désinscrire que lui-même
        if (utilisateur.getRole() == Role.APPRENANT && !utilisateur.getId().equals(idApprenant)) {
            throw new AccessDeniedException("Un apprenant ne peut pas désinscrire un autre apprenant");
        }

        return inscriptionService.desinscrire(idCours, idApprenant);
    }

    // 🔽 Auto-désinscription
    @DeleteMapping("/cours/{idCours}/me")
    @PreAuthorize("hasRole('APPRENANT')")
    public InscriptionDto selfDesinscrire(@PathVariable Long idCours, Authentication auth) {
        Long id = utilisateurService.trouverParEmail(auth.getName())
                .orElseThrow().getId();
        return inscriptionService.desinscrire(idCours, id);
    }

    // 🔽 Lister toutes les inscriptions actives d’un cours
    @GetMapping("/cours/{idCours}")
    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
    public List<InscriptionDto> inscriptionsParCours(@PathVariable @NotNull Long idCours) {
        return inscriptionService.getInscriptionsParCours(idCours);
    }

    // 🔽 Lister toutes les inscriptions actives d’un apprenant
    @GetMapping("/apprenant/{idApprenant}")
    @PreAuthorize("hasAnyRole('ADMIN','APPRENANT')")
    public List<InscriptionDto> inscriptionsParApprenant(@PathVariable Long idApprenant,
                                                         Authentication auth) {
        Utilisateur utilisateur = utilisateurService.trouverParEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Utilisateur non trouvé"));

        // Un apprenant peut consulter uniquement ses propres inscriptions
        if (utilisateur.getRole() == Role.APPRENANT && !utilisateur.getId().equals(idApprenant)) {
            throw new AccessDeniedException("Un apprenant ne peut voir que ses propres inscriptions");
        }

        return inscriptionService.getInscriptionsParApprenant(idApprenant);
    }
}
