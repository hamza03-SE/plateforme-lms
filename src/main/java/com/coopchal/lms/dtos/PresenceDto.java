package com.coopchal.lms.dtos;

import lombok.Data;

@Data
public class PresenceDto {
    private Long id;
    private Long idSession;
    private Long idApprenant;
    private String nomApprenant;
    private boolean present;
}