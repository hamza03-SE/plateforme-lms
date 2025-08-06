package com.coopchal.lms.repositories;
import com.coopchal.lms.models.Inscription;
import com.coopchal.lms.models.Cours;
import com.coopchal.lms.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    Optional<Inscription> findByCoursAndApprenant(Cours cours, Utilisateur apprenant);

    List<Inscription> findByCours(Cours cours);

    List<Inscription> findByApprenant(Utilisateur apprenant);

    List<Inscription> findByCoursIdAndActiveTrue(Long idCours);

    List<Inscription> findByApprenantIdAndActiveTrue(Long idApprenant);

    boolean existsByCoursAndApprenantAndActiveTrue(Cours cours, Utilisateur apprenant);
}
