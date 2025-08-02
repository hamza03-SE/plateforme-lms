package com.coopchal.lms.dtos;

import java.util.List;

public record CoursDto(
        Long id,
        String titre,
        String description,
        String formateurNomComplet,
        int nbApprenants,
        List<ApprenantDto> apprenants
) {
}
