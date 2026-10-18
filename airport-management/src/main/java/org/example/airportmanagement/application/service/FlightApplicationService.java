package org.example.airportmanagement.application.service;

import org.example.airportmanagement.application.command.FlightCommand;
import org.example.airportmanagement.application.exception.ConflictException;
import org.example.airportmanagement.application.exception.ResourceNotFoundException;
import org.example.airportmanagement.application.port.out.AircraftStore;
import org.example.airportmanagement.application.port.out.BookingStore;
import org.example.airportmanagement.application.port.out.FlightStore;
import org.example.airportmanagement.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class FlightApplicationService {
    private final FlightStore flights;
    private final AircraftStore aircrafts;
    private final BookingStore bookings;

    public FlightApplicationService(FlightStore flights, AircraftStore aircrafts, BookingStore bookings) {
        this.flights = flights;
        this.aircrafts = aircrafts;
        this.bookings = bookings;
    }

    @Transactional(readOnly = true)
    public List<Flight> findAll() {
        return flights.findAll().stream().sorted(Comparator.comparing(Flight::departureTime)).toList();
    }

    @Transactional(readOnly = true)
    public Flight findById(Long id) { return flights.findById(id).orElseThrow(() -> new ResourceNotFoundException("Flight", id)); }

    public Flight create(FlightCommand command) {
        Aircraft aircraft = findAvailableAircraft(command.aircraftId());
        ensureScheduleAvailable(aircraft.id(), command, null);
        return flights.save(toFlight(null, command, aircraft));
    }

    public Flight update(Long id, FlightCommand command) {
        findById(id);
        Aircraft aircraft = findAvailableAircraft(command.aircraftId());
        if (bookings.countActiveForFlight(id) > aircraft.capacity()) {
            throw new ConflictException("Aircraft capacity is lower than existing bookings");
        }
        ensureScheduleAvailable(aircraft.id(), command, id);
        return flights.save(toFlight(id, command, aircraft));
    }

    public void delete(Long id) { flights.delete(findById(id)); }

    private Aircraft findAvailableAircraft(Long id) {
        Aircraft aircraft = aircrafts.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aircraft", id));
        if (aircraft.status() != AircraftStatus.AVAILABLE) {
            throw new ConflictException("Aircraft " + aircraft.registration() + " is not available");
        }
        return aircraft;
    }

    private void ensureScheduleAvailable(Long aircraftId, FlightCommand command, Long ignoredId) {
        if (command.status() == FlightStatus.CANCELLED) return;
        boolean overlap = flights.findByAircraftId(aircraftId).stream()
                .filter(flight -> !flight.id().equals(ignoredId))
                .filter(flight -> flight.status() != FlightStatus.CANCELLED)
                .anyMatch(flight -> flight.departureTime().isBefore(command.arrivalTime())
                        && flight.arrivalTime().isAfter(command.departureTime()));
        if (overlap) throw new ConflictException("Aircraft already has a flight scheduled during this time");
    }

    private Flight toFlight(Long id, FlightCommand command, Aircraft aircraft) {
        return new Flight(id, command.flightNumber(), command.origin(), command.destination(),
                command.departureTime(), command.arrivalTime(), command.status(), aircraft.id(), aircraft.registration());
    }
}
