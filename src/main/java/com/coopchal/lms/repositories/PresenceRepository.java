package com.coopchal.lms.repositories;

import com.coopchal.lms.models.Presence;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.models.Cours;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PresenceRepository extends JpaRepository<Presence, Long> {
    List<Presence> findByApprenantAndCours(Utilisateur apprenant, Cours cours);
    List<Presence> findByCoursAndDate(Cours cours, LocalDate date);
}
