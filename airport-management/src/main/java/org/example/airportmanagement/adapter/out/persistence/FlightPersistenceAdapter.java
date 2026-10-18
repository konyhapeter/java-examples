package org.example.airportmanagement.adapter.out.persistence;

import org.example.airportmanagement.adapter.out.persistence.entity.AircraftEntity;
import org.example.airportmanagement.adapter.out.persistence.entity.FlightEntity;
import org.example.airportmanagement.adapter.out.persistence.repository.AircraftJpaRepository;
import org.example.airportmanagement.adapter.out.persistence.repository.FlightJpaRepository;
import org.example.airportmanagement.application.exception.ResourceNotFoundException;
import org.example.airportmanagement.application.port.out.FlightStore;
import org.example.airportmanagement.domain.Flight;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FlightPersistenceAdapter implements FlightStore {
    private final FlightJpaRepository repository;
    private final AircraftJpaRepository aircraftRepository;

    public FlightPersistenceAdapter(FlightJpaRepository repository, AircraftJpaRepository aircraftRepository) {
        this.repository = repository;
        this.aircraftRepository = aircraftRepository;
    }

    @Override
    public List<Flight> findAll() { return repository.findAll().stream().map(this::toDomain).toList(); }

    @Override
    public List<Flight> findByAircraftId(Long aircraftId) {
        return repository.findByAircraftId(aircraftId).stream().map(this::toDomain).toList();
    }

    @Override
    public int capacityForFlight(Long flightId) {
        Integer capacity = repository.capacityForFlight(flightId);
        if (capacity == null) throw new ResourceNotFoundException("Flight", flightId);
        return capacity;
    }

    @Override
    public Optional<Flight> findById(Long id) { return repository.findById(id).map(this::toDomain); }

    @Override
    public Flight save(Flight flight) {
        AircraftEntity aircraft = aircraftRepository.findById(flight.aircraftId())
                .orElseThrow(() -> new ResourceNotFoundException("Aircraft", flight.aircraftId()));
        FlightEntity entity = flight.id() == null
                ? new FlightEntity(flight.flightNumber(), flight.origin(), flight.destination(), flight.departureTime(),
                        flight.arrivalTime(), flight.status(), aircraft)
                : repository.findById(flight.id()).orElseThrow(() -> new ResourceNotFoundException("Flight", flight.id()));
        if (flight.id() != null) entity.update(flight.flightNumber(), flight.origin(), flight.destination(),
                flight.departureTime(), flight.arrivalTime(), flight.status(), aircraft);
        return toDomain(repository.save(entity));
    }

    @Override
    public void delete(Flight flight) {
        repository.delete(repository.findById(flight.id())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", flight.id())));
    }

    private Flight toDomain(FlightEntity entity) {
        return new Flight(entity.getId(), entity.getFlightNumber(), entity.getOrigin(), entity.getDestination(),
                entity.getDepartureTime(), entity.getArrivalTime(), entity.getStatus(),
                entity.getAircraft().getId(), entity.getAircraft().getRegistration());
    }
}
