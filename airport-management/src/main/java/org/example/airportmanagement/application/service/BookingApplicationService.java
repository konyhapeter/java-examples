package org.example.airportmanagement.application.service;

import org.example.airportmanagement.application.command.BookingCommand;
import org.example.airportmanagement.application.exception.ConflictException;
import org.example.airportmanagement.application.exception.ResourceNotFoundException;
import org.example.airportmanagement.application.port.out.BookingStore;
import org.example.airportmanagement.application.port.out.FlightStore;
import org.example.airportmanagement.application.port.out.PassengerStore;
import org.example.airportmanagement.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookingApplicationService {
    private final BookingStore bookings;
    private final PassengerStore passengers;
    private final FlightStore flights;

    public BookingApplicationService(BookingStore bookings, PassengerStore passengers, FlightStore flights) {
        this.bookings = bookings;
        this.passengers = passengers;
        this.flights = flights;
    }

    @Transactional(readOnly = true)
    public List<Booking> findAll() { return bookings.findAll(); }

    @Transactional(readOnly = true)
    public Booking findById(Long id) { return bookings.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking", id)); }

    @Transactional(readOnly = true)
    public List<Booking> findByFlightId(Long flightId) {
        if (flights.findById(flightId).isEmpty()) throw new ResourceNotFoundException("Flight", flightId);
        return bookings.findByFlightId(flightId);
    }

    public Booking create(BookingCommand command) {
        Passenger passenger = passengers.findById(command.passengerId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger", command.passengerId()));
        Flight flight = flights.findById(command.flightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", command.flightId()));
        validateBooking(command, flight, null);
        return bookings.save(toBooking(null, command, passenger, flight));
    }

    public Booking update(Long id, BookingCommand command) {
        Booking current = findById(id);
        Passenger passenger = passengers.findById(command.passengerId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger", command.passengerId()));
        Flight flight = flights.findById(command.flightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", command.flightId()));
        validateBooking(command, flight, current);
        return bookings.save(toBooking(id, command, passenger, flight));
    }

    public void delete(Long id) { bookings.delete(findById(id)); }

    private void validateBooking(BookingCommand command, Flight flight, Booking current) {
        if (flight.status() == FlightStatus.CANCELLED) throw new ConflictException("Bookings cannot be made on a cancelled flight");
        if (command.status() == BookingStatus.CANCELLED) return;

        long activeBookings = bookings.countActiveForFlight(flight.id());
        if (current != null && current.status() != BookingStatus.CANCELLED && current.flightId().equals(flight.id())) {
            activeBookings--;
        }
        if (activeBookings >= getCapacity(flight)) {
            throw new ConflictException("Flight has reached aircraft passenger capacity");
        }
        boolean passengerAlreadyBooked = bookings.existsActivePassenger(flight.id(), command.passengerId());
        boolean samePassenger = current != null && current.status() != BookingStatus.CANCELLED
                && current.flightId().equals(flight.id()) && current.passengerId().equals(command.passengerId());
        if (passengerAlreadyBooked && !samePassenger) {
            throw new ConflictException("Passenger already has an active booking on this flight");
        }
        boolean seatAlreadyTaken = bookings.existsActiveSeat(flight.id(), command.seatNumber());
        boolean sameSeat = current != null && current.status() != BookingStatus.CANCELLED
                && current.flightId().equals(flight.id()) && current.seatNumber().equalsIgnoreCase(command.seatNumber());
        if (seatAlreadyTaken && !sameSeat) throw new ConflictException("Seat is already booked on this flight");
    }

    private int getCapacity(Flight flight) {
        return flights.capacityForFlight(flight.id());
    }

    private Booking toBooking(Long id, BookingCommand command, Passenger passenger, Flight flight) {
        return new Booking(id, passenger.id(), passenger.firstName() + " " + passenger.lastName(), flight.id(),
                flight.flightNumber(), command.seatNumber(), command.status());
    }
}
