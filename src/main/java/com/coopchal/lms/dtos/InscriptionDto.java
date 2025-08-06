package com.coopchal.lms.dtos;

import com.coopchal.lms.models.Inscription;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InscriptionDto {

    private Long id;
    private Long idCours;
    private Long idApprenant;
    private boolean active;
    private LocalDateTime dateInscription;
    private LocalDateTime dateDesinscription;

    public static InscriptionDto fromEntity(Inscription inscription) {
        InscriptionDto dto = new InscriptionDto();
        dto.setId(inscription.getId());
        dto.setIdCours(inscription.getCours().getId());
        dto.setIdApprenant(inscription.getApprenant().getId());
        dto.setActive(inscription.isActive());
        dto.setDateInscription(inscription.getDateInscription());
        dto.setDateDesinscription(inscription.getDateDesinscription());
        return dto;
    }
}
