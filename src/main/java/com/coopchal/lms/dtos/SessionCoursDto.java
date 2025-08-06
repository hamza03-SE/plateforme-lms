package com.coopchal.lms.dtos;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SessionCoursDto {
    private Long id;
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private String salle;
    private Long idCours;
    private Long idFormateur;
    private List<PresenceDto> presences;
}

