package com.coopchal.lms.dtos;
import com.coopchal.lms.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private Role role;
}
