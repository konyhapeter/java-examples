package org.example.airportmanagement.application.service;

import org.example.airportmanagement.application.command.AircraftCommand;
import org.example.airportmanagement.application.exception.ConflictException;
import org.example.airportmanagement.application.exception.ResourceNotFoundException;
import org.example.airportmanagement.application.port.out.AircraftStore;
import org.example.airportmanagement.application.port.out.BookingStore;
import org.example.airportmanagement.application.port.out.FlightStore;
import org.example.airportmanagement.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AircraftApplicationService {
    private final AircraftStore aircrafts;
    private final FlightStore flights;
    private final BookingStore bookings;

    public AircraftApplicationService(AircraftStore aircrafts, FlightStore flights, BookingStore bookings) {
        this.aircrafts = aircrafts;
        this.flights = flights;
        this.bookings = bookings;
    }

    @Transactional(readOnly = true)
    public List<Aircraft> findAll() { return aircrafts.findAll(); }

    @Transactional(readOnly = true)
    public Aircraft findById(Long id) { return aircrafts.findById(id).orElseThrow(() -> new ResourceNotFoundException("Aircraft", id)); }

    public Aircraft create(AircraftCommand command) {
        return aircrafts.save(new Aircraft(null, command.registration(), command.model(), command.capacity(), command.status()));
    }

    public Aircraft update(Long id, AircraftCommand command) {
        Aircraft current = findById(id);
        for (Flight flight : flights.findByAircraftId(id)) {
            if (bookings.countActiveForFlight(flight.id()) > command.capacity()) {
                throw new ConflictException("Aircraft capacity cannot be lower than existing bookings");
            }
            if (command.status() != AircraftStatus.AVAILABLE && flight.status() != FlightStatus.CANCELLED) {
                throw new ConflictException("Aircraft assigned to an active flight must remain available");
            }
        }
        return aircrafts.save(new Aircraft(current.id(), command.registration(), command.model(),
                command.capacity(), command.status()));
    }

    public void delete(Long id) { aircrafts.delete(findById(id)); }
}
