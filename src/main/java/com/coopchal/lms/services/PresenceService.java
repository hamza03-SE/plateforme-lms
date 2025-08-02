package com.coopchal.lms.services;
import com.coopchal.lms.models.Cours;
import com.coopchal.lms.models.Presence;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.repositories.CoursRepository;
import com.coopchal.lms.repositories.PresenceRepository;
import com.coopchal.lms.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final CoursRepository coursRepository;

    public Presence marquerPresenceManuelle(Long idApprenant, Long idCours, LocalDate date, boolean estPresent) {
        Utilisateur apprenant = utilisateurRepository.findById(idApprenant)
                .orElseThrow();
        Cours cours = coursRepository.findById(idCours)
                .orElseThrow();

        Presence presence = new Presence();
        presence.setApprenant(apprenant);
        presence.setCours(cours);
        presence.setDate(date);
        presence.setEstPresent(estPresent);

        return presenceRepository.save(presence);
    }

    public Presence autoDeclarationPresence(Long idApprenant, Long idCours) {
        return marquerPresenceManuelle(idApprenant, idCours, LocalDate.now(), true);
    }

    public List<Presence> rapportParCours(Long idCours) {
        Cours cours = coursRepository.findById(idCours).orElseThrow();
        return presenceRepository.findByCoursAndDate(cours, LocalDate.now());
    }

    public List<Presence> presencesParApprenant(Long idApprenant, Long idCours) {
        Utilisateur user = utilisateurRepository.findById(idApprenant).orElseThrow();
        Cours cours = coursRepository.findById(idCours).orElseThrow();
        return presenceRepository.findByApprenantAndCours(user, cours);
    }
}
