package org.example.airportmanagement.domain;

public record Booking(Long id, Long passengerId, String passengerName, Long flightId, String flightNumber,
                      String seatNumber, BookingStatus status) {
    public Booking {
        if (passengerId == null || flightId == null) throw new IllegalArgumentException("Passenger and flight are required");
        if (seatNumber == null || seatNumber.isBlank()) throw new IllegalArgumentException("Seat number is required");
        if (status == null) throw new IllegalArgumentException("Booking status is required");
    }
}
