package org.example.airportmanagement.application.port.out;

import org.example.airportmanagement.domain.Flight;

import java.util.List;
import java.util.Optional;

public interface FlightStore {
    List<Flight> findAll();
    List<Flight> findByAircraftId(Long aircraftId);
    int capacityForFlight(Long flightId);
    Optional<Flight> findById(Long id);
    Flight save(Flight flight);
    void delete(Flight flight);
}
