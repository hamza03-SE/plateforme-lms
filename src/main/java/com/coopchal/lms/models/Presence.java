package com.coopchal.lms.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "presences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private SessionCours session;

    @ManyToOne
    @JoinColumn(name = "apprenant_id", nullable = false)
    private Utilisateur apprenant;

    private boolean present;
    @Column(name = "date_heure")
    private LocalDateTime dateHeure;

}
