package com.coopchal.lms.controllers;

import com.coopchal.lms.dtos.UtilisateurDto;
import com.coopchal.lms.services.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/api/presences")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;

    // Récupérer les apprenants inscrits à un cours (accessible au formateur)
    @GetMapping("/cours/{idCours}/apprenants")
    @PreAuthorize("hasRole('FORMATEUR')")
    public ResponseEntity<List<UtilisateurDto>> getApprenantsParCours(@PathVariable Long idCours) {
        List<UtilisateurDto> apprenants = presenceService.getApprenantsParCours(idCours);
        return ResponseEntity.ok(apprenants);
    }

    // Marquer la présence manuelle (formateur)
    @PostMapping("/sessions/{idSession}")
    @PreAuthorize("hasRole('FORMATEUR')")
    public ResponseEntity<String> marquerPresence(@PathVariable Long idSession, @RequestBody List<Long> idApprenants) {
        presenceService.marquerPresenceManuelle(idSession, idApprenants);
        return ResponseEntity.ok("Présence marquée avec succès");
    }

    // Auto déclaration présence par apprenant
    @PostMapping("/sessions/{idSession}/me")
    @PreAuthorize("hasRole('APPRENANT')")
    public ResponseEntity<String> autoPresence(@PathVariable Long idSession, Authentication auth) {
        presenceService.autoDeclaration(idSession, auth.getName());
        return ResponseEntity.ok("Présence auto-déclarée");
    }

    // Export CSV (formateur et admin)
    @GetMapping("/sessions/{idSession}/export")
    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
    public ResponseEntity<InputStreamResource> exporterPresence(@PathVariable Long idSession) {
        ByteArrayInputStream in = presenceService.exporterCsv(idSession);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=presence_" + idSession + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(in));
    }
}
