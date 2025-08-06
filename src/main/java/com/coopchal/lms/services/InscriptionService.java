package com.coopchal.lms.services;

import com.coopchal.lms.dtos.InscriptionDto;
import com.coopchal.lms.exceptions.ResourceNotFoundException;
import com.coopchal.lms.models.Cours;
import com.coopchal.lms.models.Inscription;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.repositories.CoursRepository;
import com.coopchal.lms.repositories.InscriptionRepository;
import com.coopchal.lms.repositories.UtilisateurRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CoursRepository coursRepository;

    @Transactional
    public InscriptionDto inscrire(Long idCours, Long idApprenant) {
        Utilisateur apprenant = utilisateurRepository.findById(idApprenant)
                .orElseThrow(() -> new ResourceNotFoundException("Apprenant non trouvé"));

        Cours cours = coursRepository.findById(idCours)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        // Vérifie si déjà inscrit
        Inscription inscription = inscriptionRepository.findByCoursAndApprenant(cours, apprenant)
                .orElseGet(() -> {
                    Inscription i = new Inscription();
                    i.setApprenant(apprenant);
                    i.setCours(cours);
                    return i;
                });

        inscription.reactiver(); // active = true
        inscriptionRepository.save(inscription);

        return InscriptionDto.fromEntity(inscription);
    }

    @Transactional
    public InscriptionDto desinscrire(Long idCours, Long idApprenant) {
        Utilisateur apprenant = utilisateurRepository.findById(idApprenant)
                .orElseThrow(() -> new ResourceNotFoundException("Apprenant non trouvé"));

        Cours cours = coursRepository.findById(idCours)
                .orElseThrow(() -> new ResourceNotFoundException("Cours non trouvé"));

        Inscription inscription = inscriptionRepository.findByCoursAndApprenant(cours, apprenant)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription non trouvée"));

        inscription.desinscrire();
        inscriptionRepository.save(inscription);

        return InscriptionDto.fromEntity(inscription);
    }

    public List<InscriptionDto> getInscriptionsParCours(Long idCours) {
        return inscriptionRepository.findByCoursIdAndActiveTrue(idCours)
                .stream()
                .map(InscriptionDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<InscriptionDto> getInscriptionsParApprenant(Long idApprenant) {
        return inscriptionRepository.findByApprenantIdAndActiveTrue(idApprenant)
                .stream()
                .map(InscriptionDto::fromEntity)
                .collect(Collectors.toList());
    }
}
