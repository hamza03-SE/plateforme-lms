package com.coopchal.lms.services;

import com.coopchal.lms.dtos.SessionCoursDto;
import com.coopchal.lms.exceptions.ResourceNotFoundException;
import com.coopchal.lms.mappers.SessionMapper;
import com.coopchal.lms.models.Cours;
import com.coopchal.lms.models.SessionCours;
import com.coopchal.lms.repositories.CoursRepository;
import com.coopchal.lms.repositories.SessionCoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionCoursService {

    private final SessionCoursRepository sessionCoursRepository;
    private final CoursRepository coursRepository;
    private final SessionMapper sessionMapper;

    /**
     * Crée une nouvelle session de cours après avoir vérifié :
     * - l'existence du cours
     * - la disponibilité de la salle
     */
    public SessionCoursDto creerSession(Long idCours, SessionCours session) {
        // Vérifie si le cours existe
        Cours cours = coursRepository.findById(idCours)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé avec id : " + idCours));

        // Associe le formateur à la session
        session.setFormateur(cours.getFormateur());

        // Vérifie que la salle est libre durant ce créneau
        boolean salleOccupee = sessionCoursRepository.existsBySalleAndDateDebutLessThanAndDateFinGreaterThan(
                session.getSalle(),
                session.getDateFin(),
                session.getDateDebut()
        );

        if (salleOccupee) {
            throw new IllegalArgumentException("La salle " + session.getSalle() + " est déjà occupée pendant cette période.");
        }

        // Enregistre et retourne le DTO
        SessionCours savedSession = sessionCoursRepository.save(session);
        return sessionMapper.toDto(savedSession);
    }

    /**
     * Liste les sessions associées à un cours donné
     */
    public List<SessionCoursDto> sessionsParCours(Long idCours) {
        return sessionCoursRepository.findByCoursId(idCours).stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Supprime une session par son identifiant
     */
    public void supprimer(Long idSession) {
        if (!sessionCoursRepository.existsById(idSession)) {
            throw new ResourceNotFoundException("Session non trouvée avec id : " + idSession);
        }
        sessionCoursRepository.deleteById(idSession);
    }

    /**
     * Récupère une session avec les apprenants du cours (évite LazyInitializationException)
     */
    public SessionCours getSessionAvecApprenants(Long sessionId) {
        return sessionCoursRepository.findWithApprenantsById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée avec ID : " + sessionId));
    }
}
