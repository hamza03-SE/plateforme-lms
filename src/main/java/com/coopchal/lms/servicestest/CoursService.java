package com.coopchal.lms.servicestest;

import com.coopchal.lms.dtos.ApprenantDto;
import com.coopchal.lms.dtos.CoursDto;
import com.coopchal.lms.models.Cours;
import com.coopchal.lms.enums.Role;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.repositories.CoursRepository;
import com.coopchal.lms.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoursService {

    private final CoursRepository coursRepository;
    private final UtilisateurRepository utilisateurRepository;

    public List<CoursDto> getAll() {
        return coursRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CoursDto creerCours(Cours cours, Long idFormateur) {
        Utilisateur formateur = utilisateurRepository.findById(idFormateur)
                .orElseThrow(() -> new RuntimeException("Formateur introuvable"));
        if (!formateur.getRole().equals(Role.FORMATEUR)) {
            throw new RuntimeException("L'utilisateur n'est pas un formateur");
        }
        cours.setFormateur(formateur);
        return toDto(coursRepository.save(cours));
    }

    @Transactional
    public CoursDto inscrireApprenant(Long idCours, Long idApprenant) {
        Cours cours = coursRepository.findById(idCours)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));

        Utilisateur apprenant = utilisateurRepository.findById(idApprenant)
                .orElseThrow(() -> new RuntimeException("Apprenant introuvable"));

        if (!cours.getApprenants().contains(apprenant)) {
            cours.getApprenants().add(apprenant);
            apprenant.getCoursInscrits().add(cours);
            utilisateurRepository.save(apprenant);
        }

        cours.getApprenants().size(); // Force l'initialisation
        return toDto(cours);
    }

    @Transactional(readOnly = true)
    public List<CoursDto> listerCoursFormateur(Long idFormateur) {
        return coursRepository.findAll().stream()
                .filter(c -> c.getFormateur().getId().equals(idFormateur))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CoursDto> listerCoursApprenant(Long idApprenant) {
        Utilisateur u = utilisateurRepository.findById(idApprenant)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        u.getCoursInscrits().size(); // Charger la liste
        return u.getCoursInscrits().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    @Transactional
    public boolean estApprenantInscrit(Long idCours, Long idApprenant) {
        return coursRepository.findById(idCours)
                .map(c -> c.getApprenants().stream()
                        .anyMatch(a -> a.getId().equals(idApprenant)))
                .orElse(false);
    }

    @Transactional
    public CoursDto desinscrireApprenant(Long idCours, Long idApprenant) {
        Cours cours = coursRepository.findById(idCours)
                .orElseThrow(() -> new RuntimeException("Cours introuvable"));

        Utilisateur apprenant = utilisateurRepository.findById(idApprenant)
                .orElseThrow(() -> new RuntimeException("Apprenant introuvable"));

        if (cours.getApprenants().contains(apprenant)) {
            cours.getApprenants().remove(apprenant);
            apprenant.getCoursInscrits().remove(cours);
            utilisateurRepository.save(apprenant);
        }

        cours.getApprenants().size(); // Charger pour éviter LazyInit
        return toDto(cours);
    }

    private CoursDto toDto(Cours cours) {
        List<ApprenantDto> apprenants = cours.getApprenants().stream()
                .map(a -> new ApprenantDto(a.getId(), a.getNom(), a.getPrenom(), a.getEmail()))
                .collect(Collectors.toList());

        String formateurNomComplet = cours.getFormateur().getNom() + " " + cours.getFormateur().getPrenom();

        return new CoursDto(
                cours.getId(),
                cours.getTitre(),
                cours.getDescription(),
                formateurNomComplet,
                apprenants.size(),
                apprenants
        );
    }
}
