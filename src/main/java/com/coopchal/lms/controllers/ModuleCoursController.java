package com.coopchal.lms.controllers;

import com.coopchal.lms.models.ModuleCours;
import com.coopchal.lms.enums.Role;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.services.CoursService;
import com.coopchal.lms.services.MinioService;
import com.coopchal.lms.services.ModuleCoursService;
import com.coopchal.lms.services.UtilisateurService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleCoursController {

    private final ModuleCoursService moduleCoursService;
    private final MinioService minioService;

    private final UtilisateurService utilisateurService;
    private final CoursService coursService;
    @PostMapping(value = "/cours/{idCours}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
    public ModuleCours add(
            @PathVariable Long idCours,
            @RequestPart("module") String moduleJson,
            @RequestPart(value = "fichier", required = false) MultipartFile fichier,
            @RequestParam(value = "bucket", required = false) String bucket  // <- choix du bucket côté client
    ) throws JsonProcessingException {
        ModuleCours module = new ObjectMapper().readValue(moduleJson, ModuleCours.class);

        // Si aucun bucket spécifié, par défaut on met dans documents
        String bucketToUse = (bucket != null && !bucket.isBlank()) ? bucket : "documents";

        if (fichier != null) {
            return moduleCoursService.ajouterAvecFichier(idCours, module, fichier, bucketToUse);
        }
        return moduleCoursService.ajouterModule(idCours, module);
    }

    @GetMapping("/cours/{idCours}")
    public List<ModuleCours> list(@PathVariable Long idCours) {
        return moduleCoursService.modulesParCours(idCours);
    }

    @DeleteMapping("/{idModule}")
    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
    public void delete(@PathVariable Long idModule) {
        moduleCoursService.supprimerModule(idModule);
    }

    @GetMapping("/download")
    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN','APPRENANT')")
    public ResponseEntity<InputStreamResource> downloadFichier(
            @RequestParam String chemin,
            @RequestParam Long idCours,
            Principal principal
    ) {
        Utilisateur utilisateur = utilisateurService.getByEmail(principal.getName());

        // Vérifie si le fichier appartient réellement au cours
        if (!moduleCoursService.fichierAppartientAuCours(idCours, chemin)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Vérifie si l'apprenant est inscrit
        if (utilisateur.getRole().equals(Role.APPRENANT)) {
            boolean inscrit = coursService.estApprenantInscrit(idCours, utilisateur.getId());
            if (!inscrit) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        // Téléchargement du fichier
        InputStream fichierStream = minioService.getFichier(chemin);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + chemin.substring(chemin.lastIndexOf('/') + 1) + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(fichierStream));
    }
}
