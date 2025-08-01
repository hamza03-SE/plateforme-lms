package com.coopchal.lms.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cours")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String description;
    private String imageUrl;

    @ManyToOne
    @JsonIgnoreProperties({"coursInscrits", "motDePasse"})
    private Utilisateur formateur;

    @ManyToMany
    @JoinTable(
            name = "cours_apprenants",
            joinColumns = @JoinColumn(name = "cours_id"),
            inverseJoinColumns = @JoinColumn(name = "apprenant_id")
    )
    @JsonIgnore
    private List<Utilisateur> apprenants = new ArrayList<>(); // ✅ important

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<ModuleCours> modules = new ArrayList<>(); // ✅ aussi important
}
