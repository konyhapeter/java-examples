package org.example.airportmanagement.adapter.out.persistence;

import org.example.airportmanagement.adapter.out.persistence.entity.PassengerEntity;
import org.example.airportmanagement.adapter.out.persistence.repository.PassengerJpaRepository;
import org.example.airportmanagement.application.exception.ResourceNotFoundException;
import org.example.airportmanagement.application.port.out.PassengerStore;
import org.example.airportmanagement.domain.Passenger;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PassengerPersistenceAdapter implements PassengerStore {
    private final PassengerJpaRepository repository;

    public PassengerPersistenceAdapter(PassengerJpaRepository repository) { this.repository = repository; }

    @Override
    public List<Passenger> findAll() { return repository.findAll().stream().map(this::toDomain).toList(); }

    @Override
    public Optional<Passenger> findById(Long id) { return repository.findById(id).map(this::toDomain); }

    @Override
    public Passenger save(Passenger passenger) {
        PassengerEntity entity = passenger.id() == null
                ? new PassengerEntity(passenger.firstName(), passenger.lastName(), passenger.email(), passenger.passportNumber())
                : repository.findById(passenger.id()).orElseThrow(() -> new ResourceNotFoundException("Passenger", passenger.id()));
        if (passenger.id() != null) entity.update(passenger.firstName(), passenger.lastName(), passenger.email(), passenger.passportNumber());
        return toDomain(repository.save(entity));
    }

    @Override
    public void delete(Passenger passenger) {
        repository.delete(repository.findById(passenger.id())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger", passenger.id())));
    }

    private Passenger toDomain(PassengerEntity entity) {
        return new Passenger(entity.getId(), entity.getFirstName(), entity.getLastName(),
                entity.getEmail(), entity.getPassportNumber());
    }
}
