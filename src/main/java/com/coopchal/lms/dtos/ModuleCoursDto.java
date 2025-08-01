package com.coopchal.lms.dtos;


import com.coopchal.lms.enums.TypeContenu;
import lombok.Data;

@Data
public class ModuleCoursDto {
    private Long id;
    private String titre;
    private String description;
    private TypeContenu type;
    private String urlFichier;
}
