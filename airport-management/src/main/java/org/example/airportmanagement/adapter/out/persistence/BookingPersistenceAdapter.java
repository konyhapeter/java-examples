package org.example.airportmanagement.adapter.out.persistence;

import org.example.airportmanagement.adapter.out.persistence.entity.BookingEntity;
import org.example.airportmanagement.adapter.out.persistence.entity.FlightEntity;
import org.example.airportmanagement.adapter.out.persistence.entity.PassengerEntity;
import org.example.airportmanagement.adapter.out.persistence.repository.BookingJpaRepository;
import org.example.airportmanagement.adapter.out.persistence.repository.FlightJpaRepository;
import org.example.airportmanagement.adapter.out.persistence.repository.PassengerJpaRepository;
import org.example.airportmanagement.application.exception.ResourceNotFoundException;
import org.example.airportmanagement.application.port.out.BookingStore;
import org.example.airportmanagement.domain.Booking;
import org.example.airportmanagement.domain.BookingStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class BookingPersistenceAdapter implements BookingStore {
    private final BookingJpaRepository repository;
    private final PassengerJpaRepository passengerRepository;
    private final FlightJpaRepository flightRepository;

    public BookingPersistenceAdapter(BookingJpaRepository repository, PassengerJpaRepository passengerRepository,
                                    FlightJpaRepository flightRepository) {
        this.repository = repository;
        this.passengerRepository = passengerRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public List<Booking> findAll() { return repository.findAll().stream().map(this::toDomain).toList(); }

    @Override
    public List<Booking> findByFlightId(Long flightId) {
        return repository.findByFlightId(flightId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Booking> findById(Long id) { return repository.findById(id).map(this::toDomain); }

    @Override
    public long countActiveForFlight(Long flightId) {
        return repository.countByFlightIdAndStatusNot(flightId, BookingStatus.CANCELLED);
    }

    @Override
    public boolean existsActivePassenger(Long flightId, Long passengerId) {
        return repository.existsByFlightIdAndPassengerIdAndStatusNot(flightId, passengerId, BookingStatus.CANCELLED);
    }

    @Override
    public boolean existsActiveSeat(Long flightId, String seatNumber) {
        return repository.existsByFlightIdAndSeatNumberIgnoreCaseAndStatusNot(flightId, seatNumber, BookingStatus.CANCELLED);
    }

    @Override
    public Booking save(Booking booking) {
        PassengerEntity passenger = passengerRepository.findById(booking.passengerId())
                .orElseThrow(() -> new ResourceNotFoundException("Passenger", booking.passengerId()));
        FlightEntity flight = flightRepository.findById(booking.flightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight", booking.flightId()));
        BookingEntity entity = booking.id() == null
                ? new BookingEntity(passenger, flight, booking.seatNumber(), booking.status())
                : repository.findById(booking.id()).orElseThrow(() -> new ResourceNotFoundException("Booking", booking.id()));
        if (booking.id() != null) entity.update(passenger, flight, booking.seatNumber(), booking.status());
        return toDomain(repository.save(entity));
    }

    @Override
    public void delete(Booking booking) {
        repository.delete(repository.findById(booking.id())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", booking.id())));
    }

    private Booking toDomain(BookingEntity entity) {
        return new Booking(entity.getId(), entity.getPassenger().getId(),
                entity.getPassenger().getFirstName() + " " + entity.getPassenger().getLastName(),
                entity.getFlight().getId(), entity.getFlight().getFlightNumber(),
                entity.getSeatNumber(), entity.getStatus());
    }
}
