package com.coopchal.lms.services;

import com.coopchal.lms.dtos.UtilisateurDto;
import com.coopchal.lms.exceptions.ResourceNotFoundException;
import com.coopchal.lms.models.Presence;
import com.coopchal.lms.models.SessionCours;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.repositories.CoursRepository;
import com.coopchal.lms.repositories.PresenceRepository;
import com.coopchal.lms.repositories.SessionCoursRepository;
import com.coopchal.lms.repositories.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final SessionCoursRepository sessionCoursRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CoursRepository coursRepository;
    private final SessionCoursService sessionCoursService;

    private static final Logger log = LoggerFactory.getLogger(PresenceService.class);

    // 1. Récupérer les apprenants inscrits au cours
    @Transactional
    public List<UtilisateurDto> getApprenantsParCours(Long idCours) {
        return coursRepository.findById(idCours)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé avec id : " + idCours))
                .getApprenants()
                .stream()
                .map(UtilisateurDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 2. Marquer la présence manuelle
    @Transactional
    public void marquerPresenceManuelle(Long idSession, List<Long> idApprenants) {
        log.info("Formateur marque la présence pour la session {} et apprenants {}", idSession, idApprenants);
        SessionCours session = sessionCoursRepository.findById(idSession)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        for (Long idApprenant : idApprenants) {
            Utilisateur apprenant = utilisateurRepository.findById(idApprenant)
                    .orElseThrow(() -> new ResourceNotFoundException("Apprenant non trouvé avec id : " + idApprenant));

            boolean presenceExists = presenceRepository.existsBySessionAndApprenantAndDateHeureBetween(
                    session, apprenant, startOfDay, endOfDay);

            if (!presenceExists) {
                log.info("Ajout présence : apprenant={} session={} date={}", apprenant.getId(), session.getId(), now);
                Presence presence = new Presence();
                presence.setSession(session);
                presence.setApprenant(apprenant);
                presence.setPresent(true);
                presence.setDateHeure(now);

                presenceRepository.save(presence);
            }
            else {
                log.warn("Présence déjà enregistrée pour apprenant={} session={} aujourd’hui", apprenant.getId(), session.getId());
            }
        }
    }

    // 3. Auto déclaration présence apprenant
    @Transactional
    public void autoDeclaration(Long sessionId, String email) {
        log.info("Auto-déclaration de présence par {} pour session {}", email, sessionId);
        Utilisateur apprenant = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec email : " + email));
        SessionCours session = sessionCoursRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session non trouvée avec ID : " + sessionId));

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getDateDebut()) || now.isAfter(session.getDateFin())) {
            log.warn("Tentative d'auto-déclaration hors du créneau : {} - {}", session.getDateDebut(), session.getDateFin());
            throw new IllegalStateException("Vous ne pouvez déclarer votre présence qu’entre "
                    + session.getDateDebut() + " et " + session.getDateFin());
        }
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        boolean existe = presenceRepository.existsBySessionAndApprenantAndDateHeureBetween(
                session, apprenant, startOfDay, endOfDay);

        if (!existe) {
            Presence presence = new Presence(null, session, apprenant, true, now);
            presenceRepository.save(presence);
        }
    }

    // 4. Exporter présence CSV
    @Transactional
    public ByteArrayInputStream exporterCsv(Long sessionId) {
        // Récupère la session avec les apprenants déjà chargés (grâce à @EntityGraph)
        SessionCours session = sessionCoursService.getSessionAvecApprenants(sessionId);

        // Liste des apprenants inscrits à ce cours
        List<Utilisateur> apprenants = session.getCours().getApprenants();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        writer.println("ID Session,Titre Cours,ID Formateur,Nom Formateur,Prénom Formateur,Nom Apprenant,Prénom Apprenant,Présent,Date/Heure");

        for (Utilisateur apprenant : apprenants) {
            // Cherche la présence éventuelle pour cet apprenant et cette session
            Optional<Presence> optionalPresence = presenceRepository.findBySessionAndApprenantId(session, apprenant.getId());

            boolean present = optionalPresence.map(Presence::isPresent).orElse(false);
            String dateHeure = optionalPresence.map(p -> p.getDateHeure().toString()).orElse("");

            writer.printf("%d,%s,%d,%s,%s,%s,%s,%s,%s\n",
                    session.getId(),
                    session.getCours().getTitre(),
                    session.getFormateur().getId(),
                    session.getFormateur().getNom(),
                    session.getFormateur().getPrenom(),
                    apprenant.getNom(),
                    apprenant.getPrenom(),
                    present ? "Oui" : "Non",
                    dateHeure
            );
        }

        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
