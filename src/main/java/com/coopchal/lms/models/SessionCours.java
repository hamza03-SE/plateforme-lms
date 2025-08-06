package com.coopchal.lms.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionCours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;

    private String salle; // nouvel attribut

    @ManyToOne
    @JoinColumn(name = "cours_id", nullable = false)
    @JsonIgnoreProperties({"apprenants", "modules", "formateur"})
    private Cours cours;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Presence> presences;
    @ManyToOne
    @JoinColumn(name = "id_formateur")
    private Utilisateur formateur;

}
