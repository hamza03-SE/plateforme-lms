package com.coopchal.lms.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscriptions")
@Data
@NoArgsConstructor
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "apprenant_id", nullable = false)
    private Utilisateur apprenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;

    @Column(nullable = false)
    private LocalDateTime dateInscription = LocalDateTime.now();

    private LocalDateTime dateDesinscription;

    @Column(nullable = false)
    private boolean active = true;

    // Méthodes métiers
    public void desinscrire() {
        this.active = false;
        this.dateDesinscription = LocalDateTime.now();
    }

    public void reactiver() {
        this.active = true;
        this.dateDesinscription = null;
    }
}
