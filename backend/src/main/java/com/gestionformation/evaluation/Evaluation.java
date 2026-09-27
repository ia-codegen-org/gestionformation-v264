package com.gestionformation.evaluation;

import jakarta.persistence.*;
import lombok.*;
import com.gestionformation.session.Session;
import com.gestionformation.etudiant.Etudiant;
@Entity
@Table(name = "evaluation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_by")
    private String createdBy;

    @Column(nullable = false)
    private Double note;

    @Column(nullable = false)
    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "session_id")
    private Session session;

    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

}
