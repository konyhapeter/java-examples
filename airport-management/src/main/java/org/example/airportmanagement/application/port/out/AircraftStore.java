package org.example.airportmanagement.application.port.out;

import org.example.airportmanagement.domain.Aircraft;

import java.util.List;
import java.util.Optional;

public interface AircraftStore {
    List<Aircraft> findAll();
    Optional<Aircraft> findById(Long id);
    Aircraft save(Aircraft aircraft);
    void delete(Aircraft aircraft);
}
