package com.coopchal.lms.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UtilisateurUpdateDto {

    private String nom;

    private String prenom;

    private String motDePasse; // optionnel, à encoder si présent

    // getters/setters
}
