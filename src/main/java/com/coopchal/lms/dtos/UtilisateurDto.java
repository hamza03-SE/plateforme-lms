package com.coopchal.lms.dtos;

import com.coopchal.lms.models.Utilisateur;
import lombok.Data;

@Data
public class UtilisateurDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;

    public static UtilisateurDto fromEntity(Utilisateur utilisateur) {
        UtilisateurDto dto = new UtilisateurDto();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        return dto;
    }
}
