package com.coopchal.lms.servicestest;
import com.coopchal.lms.dtos.UtilisateurUpdateDto;
import com.coopchal.lms.models.Utilisateur;
import com.coopchal.lms.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilisateurService {


    private final UtilisateurRepository utilisateurRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Utilisateur getByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable avec l'email: " + email));
    }
    public Utilisateur inscrireUtilisateur(Utilisateur utilisateur) {
        if (utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new IllegalArgumentException("Email déjà utilisé");
        }
        // Hasher le mot de passe + set rôle
        utilisateur.setMotDePasse(new BCryptPasswordEncoder().encode(utilisateur.getMotDePasse()));
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur modifierProfil(Long id, UtilisateurUpdateDto updated) {
        Utilisateur existing = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        existing.setNom(updated.getNom());
        existing.setPrenom(updated.getPrenom());

        if (updated.getMotDePasse() != null && !updated.getMotDePasse().isBlank()) {
            existing.setMotDePasse(passwordEncoder.encode(updated.getMotDePasse()));
        }

        return utilisateurRepository.save(existing);
    }

    public Utilisateur save(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public void supprimer(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public Optional<Utilisateur> trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    public List<Utilisateur> listerTous() {
        return utilisateurRepository.findAll();
    }
}
