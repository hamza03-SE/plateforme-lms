package com.coopchal.lms.repositories;

import com.coopchal.lms.models.Presence;
import com.coopchal.lms.models.SessionCours;
import com.coopchal.lms.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PresenceRepository extends JpaRepository<Presence, Long> {
    List<Presence> findBySession(SessionCours session);
    Optional<Presence> findBySessionAndApprenantId(SessionCours session, Long apprenantId);
    boolean existsBySessionAndApprenantAndDateHeureBetween(SessionCours session, Utilisateur apprenant, LocalDateTime start, LocalDateTime end);

}
