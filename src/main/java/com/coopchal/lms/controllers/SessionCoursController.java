package com.coopchal.lms.controllers;

import com.coopchal.lms.dtos.SessionCoursDto;
import com.coopchal.lms.exceptions.ResourceNotFoundException;
import com.coopchal.lms.models.Cours;
import com.coopchal.lms.models.SessionCours;
import com.coopchal.lms.repositories.CoursRepository;
import com.coopchal.lms.services.SessionCoursService;
import com.coopchal.lms.mappers.SessionMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionCoursController {

    private final SessionCoursService sessionCoursService;
    private final CoursRepository coursRepository;
    private final SessionMapper sessionMapper;

    @PostMapping("/cours/{idCours}")
    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
    public ResponseEntity<SessionCoursDto> createSession(@PathVariable Long idCours,
                                                         @Valid @RequestBody SessionCoursDto sessionDto) {
        // Vérifier que le cours existe (404 sinon)
        Cours cours = coursRepository.findById(idCours)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé avec id : " + idCours));

        // Mapper DTO -> Entity
        SessionCours session = sessionMapper.toEntity(sessionDto, cours);

        // Créer la session (le formateur sera injecté en service via cours.getFormateur())
        SessionCoursDto created = sessionCoursService.creerSession(idCours, session);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/cours/{idCours}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<SessionCoursDto>> getSessions(@PathVariable Long idCours) {
        List<SessionCoursDto> sessions = sessionCoursService.sessionsParCours(idCours);
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/{idSession}")
    @PreAuthorize("hasAnyRole('FORMATEUR','ADMIN')")
    public ResponseEntity<Void> deleteSession(@PathVariable Long idSession) {
        sessionCoursService.supprimer(idSession);
        return ResponseEntity.noContent().build();
    }
}
