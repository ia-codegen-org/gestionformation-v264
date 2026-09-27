package com.gestionformation.batiment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.util.List;
import com.gestionformation.exception.ResourceNotFoundException;
import com.gestionformation.security.SecurityUtils;
import com.gestionformation.salle.Salle;
import com.gestionformation.salle.SalleRepository;
import com.gestionformation.salle.SalleResponseDTO;
import com.gestionformation.salle.SalleMapper;

@Service
@Transactional
@RequiredArgsConstructor
public class BatimentService {

    private static final String BATIMENT_NOT_FOUND = "Batiment not found with id: ";

    private final BatimentRepository repository;
    private final BatimentMapper     mapper;
    private final SecurityUtils securityUtils;
    private final SalleRepository salleRepository;
    private final SalleMapper salleMapper;

    private java.util.stream.Stream<Batiment> accessibleStream() {
        if (securityUtils.isAdmin()) return repository.findAll().stream();
        final String currentUser = securityUtils.getConnectedUsername();
        if (currentUser == null) return java.util.stream.Stream.empty();
        return repository.findAll().stream()
                .filter(e ->
                    e.getCreatedBy() == null
                    || currentUser.equals(e.getCreatedBy())
                );
    }

    private void checkOwnership(Batiment entity, String message) {
        if (securityUtils.isAdmin()) return;
        final String currentUser = securityUtils.getConnectedUsername();
        if (currentUser == null) return;
        if (!(entity.getCreatedBy() == null
                    || currentUser.equals(entity.getCreatedBy()))) {
            throw new org.springframework.security.access.AccessDeniedException(message);
        }
    }

    @Transactional(readOnly = true)
    public List<BatimentResponseDTO> findAll() {
        return accessibleStream()
                .map(mapper::toResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public long count() {
        return accessibleStream().count();
    }

    @Transactional(readOnly = true)
    public BatimentResponseDTO findById(Long id) {
        Batiment entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BATIMENT_NOT_FOUND + id));
        checkOwnership(entity, "Accès refusé");
        BatimentResponseDTO dto = mapper.toResponseDTO(entity);
        return dto;
    }

    public BatimentResponseDTO create(BatimentCreateDTO dto) {
        Batiment entity = mapper.toEntity(dto);
        if (!securityUtils.isAdmin()) {
            entity.setCreatedBy(securityUtils.getConnectedUsername());
        }
        return mapper.toResponseDTO(repository.save(entity));
    }

    public BatimentResponseDTO update(Long id, BatimentUpdateDTO dto) {
        Batiment entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BATIMENT_NOT_FOUND + id));
        checkOwnership(entity, "Vous ne pouvez modifier que vos propres données");
        mapper.updateEntity(entity, dto);
        return mapper.toResponseDTO(repository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        Batiment entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BATIMENT_NOT_FOUND + id));
        checkOwnership(entity, "Vous ne pouvez supprimer que vos propres données");
        if (entity.getSalles() != null && !entity.getSalles().isEmpty()) {
            salleRepository
                    .deleteAll(entity.getSalles());
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SalleResponseDTO> findSallesByBatimentId(Long id) {
        Batiment entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        BATIMENT_NOT_FOUND + id));
        return entity.getSalles()
                .stream()
                .map(salleMapper::toResponseDTO)
                .toList();
    }
}
