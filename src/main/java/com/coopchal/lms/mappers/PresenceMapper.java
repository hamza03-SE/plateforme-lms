package com.coopchal.lms.mappers;

import com.coopchal.lms.dtos.PresenceDto;
import com.coopchal.lms.models.Presence;
import org.springframework.stereotype.Component;

@Component
public class PresenceMapper {

    public PresenceDto toDto(Presence presence) {
        PresenceDto dto = new PresenceDto();
        dto.setId(presence.getId());

        if (presence.getSession() != null) {
            dto.setIdSession(presence.getSession().getId());
        }

        if (presence.getApprenant() != null) {
            dto.setIdApprenant(presence.getApprenant().getId());
            dto.setNomApprenant(
                    presence.getApprenant().getPrenom() + " " + presence.getApprenant().getNom()
            );
        }

        dto.setPresent(presence.isPresent());
        return dto;
    }
}
