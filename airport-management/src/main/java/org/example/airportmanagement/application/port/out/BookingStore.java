package org.example.airportmanagement.application.port.out;

import org.example.airportmanagement.domain.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingStore {
    List<Booking> findAll();
    List<Booking> findByFlightId(Long flightId);
    Optional<Booking> findById(Long id);
    long countActiveForFlight(Long flightId);
    boolean existsActivePassenger(Long flightId, Long passengerId);
    boolean existsActiveSeat(Long flightId, String seatNumber);
    Booking save(Booking booking);
    void delete(Booking booking);
}
