package com.coopchal.lms.repositories;

import com.coopchal.lms.models.SessionCours;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionCoursRepository extends JpaRepository<SessionCours, Long> {
    @EntityGraph(attributePaths = {"presences"})
    List<SessionCours> findByCoursId(Long coursId);
    boolean existsBySalleAndDateDebutLessThanAndDateFinGreaterThan(String salle, LocalDateTime dateFin, LocalDateTime dateDebut);

    @EntityGraph(attributePaths = {"cours.apprenants", "formateur"})
    Optional<SessionCours> findWithApprenantsById(Long id);

}
