package org.example.airportmanagement.adapter.out.persistence.repository;

import org.example.airportmanagement.adapter.out.persistence.entity.BookingEntity;
import org.example.airportmanagement.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByFlightId(Long flightId);
    long countByFlightIdAndStatusNot(Long flightId, BookingStatus status);
    boolean existsByFlightIdAndPassengerIdAndStatusNot(Long flightId, Long passengerId, BookingStatus status);
    boolean existsByFlightIdAndSeatNumberIgnoreCaseAndStatusNot(Long flightId, String seatNumber, BookingStatus status);
}
