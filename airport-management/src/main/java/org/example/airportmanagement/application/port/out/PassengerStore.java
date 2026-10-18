package org.example.airportmanagement.application.port.out;

import org.example.airportmanagement.domain.Passenger;

import java.util.List;
import java.util.Optional;

public interface PassengerStore {
    List<Passenger> findAll();
    Optional<Passenger> findById(Long id);
    Passenger save(Passenger passenger);
    void delete(Passenger passenger);
}
