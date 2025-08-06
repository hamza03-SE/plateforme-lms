package com.coopchal.lms.mappers;

import com.coopchal.lms.dtos.PresenceDto;
import com.coopchal.lms.dtos.SessionCoursDto;
import com.coopchal.lms.models.Cours;
import com.coopchal.lms.models.Presence;
import com.coopchal.lms.models.SessionCours;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SessionMapper {

    // Convertit Entity -> DTO
    public SessionCoursDto toDto(SessionCours session) {
        SessionCoursDto dto = new SessionCoursDto();
        dto.setId(session.getId());
        dto.setDateDebut(session.getDateDebut());
        dto.setDateFin(session.getDateFin());
        dto.setSalle(session.getSalle());

        if (session.getCours() != null) {
            dto.setIdCours(session.getCours().getId());
        }

        if (session.getFormateur() != null) {
            dto.setIdFormateur(session.getFormateur().getId());
        }

        if (session.getPresences() != null) {
            dto.setPresences(
                    session.getPresences().stream()
                            .map(this::toDto)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    // Convertit DTO -> Entity (avec cours en paramètre)
    public SessionCours toEntity(SessionCoursDto dto, Cours cours) {
        SessionCours session = new SessionCours();
        session.setId(dto.getId());
        session.setDateDebut(dto.getDateDebut());
        session.setDateFin(dto.getDateFin());
        session.setSalle(dto.getSalle());
        session.setCours(cours);
        // Ne pas setter formateur ici : il sera injecté en service via cours.getFormateur()
        return session;
    }

    // Convertit Presence Entity -> DTO
    public PresenceDto toDto(Presence presence) {
        PresenceDto dto = new PresenceDto();
        dto.setId(presence.getId());
        dto.setIdSession(presence.getSession().getId());

        if (presence.getApprenant() != null) {
            dto.setIdApprenant(presence.getApprenant().getId());
            dto.setNomApprenant(presence.getApprenant().getPrenom() + " " + presence.getApprenant().getNom());
        }

        dto.setPresent(presence.isPresent());
        return dto;
    }
}
