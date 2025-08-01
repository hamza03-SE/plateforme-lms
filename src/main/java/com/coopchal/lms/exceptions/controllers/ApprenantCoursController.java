package com.coopchal.lms.exceptions.controllers;

import com.coopchal.lms.servicestest.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/apprenant/cours")
@RequiredArgsConstructor
public class ApprenantCoursController {

    private final MinioService minioService;

    @GetMapping("/download/url")
    public ResponseEntity<String> getLienSigne(@RequestParam String cheminMinio) {
        try {
            // Exemple: "/documents/modules/1234_test.pdf"
            // On veut extraire "modules/1234_test.pdf"
            String prefix = "/documents/";
            if (!cheminMinio.startsWith(prefix)) {
                return ResponseEntity.badRequest().body("Chemin invalide.");
            }

            String object = cheminMinio.substring(prefix.length());

            String url = minioService.generatePresignedUrl(object);
            return ResponseEntity.ok(url);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur: " + e.getMessage());
        }
    }
}
