package com.gestionformation.batiment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BatimentRepository extends JpaRepository<Batiment, Long> {

    // ✅ Données privées (propriétaire) + données publiques (admin)
    List<Batiment> findByCreatedByOrCreatedByIsNull(String createdBy);

    // Recherche par nom
    List<Batiment> findByNom(String nom);
    List<Batiment> findByNomContainingIgnoreCase(String nom);
    // Recherche par adresse
    List<Batiment> findByAdresse(String adresse);
    List<Batiment> findByAdresseContainingIgnoreCase(String adresse);
}
