package org.example.airportmanagement.adapter.out.persistence;

import org.example.airportmanagement.adapter.out.persistence.entity.AircraftEntity;
import org.example.airportmanagement.adapter.out.persistence.repository.AircraftJpaRepository;
import org.example.airportmanagement.application.exception.ResourceNotFoundException;
import org.example.airportmanagement.application.port.out.AircraftStore;
import org.example.airportmanagement.domain.Aircraft;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class AircraftPersistenceAdapter implements AircraftStore {
    private final AircraftJpaRepository repository;

    public AircraftPersistenceAdapter(AircraftJpaRepository repository) { this.repository = repository; }

    @Override
    public List<Aircraft> findAll() { return repository.findAll().stream().map(this::toDomain).toList(); }

    @Override
    public Optional<Aircraft> findById(Long id) { return repository.findById(id).map(this::toDomain); }

    @Override
    public Aircraft save(Aircraft aircraft) {
        AircraftEntity entity = aircraft.id() == null
                ? new AircraftEntity(aircraft.registration(), aircraft.model(), aircraft.capacity(), aircraft.status())
                : repository.findById(aircraft.id()).orElseThrow(() -> new ResourceNotFoundException("Aircraft", aircraft.id()));
        if (aircraft.id() != null) entity.update(aircraft.registration(), aircraft.model(), aircraft.capacity(), aircraft.status());
        return toDomain(repository.save(entity));
    }

    @Override
    public void delete(Aircraft aircraft) {
        repository.delete(repository.findById(aircraft.id())
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft", aircraft.id())));
    }

    private Aircraft toDomain(AircraftEntity entity) {
        return new Aircraft(entity.getId(), entity.getRegistration(), entity.getModel(),
                entity.getCapacity(), entity.getStatus());
    }
}
