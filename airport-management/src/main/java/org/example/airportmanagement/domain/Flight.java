package org.example.airportmanagement.domain;

import java.time.LocalDateTime;

public record Flight(Long id, String flightNumber, String origin, String destination,
                     LocalDateTime departureTime, LocalDateTime arrivalTime,
                     FlightStatus status, Long aircraftId, String aircraftRegistration) {
    public Flight {
        if (flightNumber == null || flightNumber.isBlank()) throw new IllegalArgumentException("Flight number is required");
        if (origin == null || origin.isBlank() || destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Flight origin and destination are required");
        }
        if (origin.trim().equalsIgnoreCase(destination.trim())) {
            throw new IllegalArgumentException("Flight origin and destination must be different");
        }
        if (departureTime == null || arrivalTime == null || !arrivalTime.isAfter(departureTime)) {
            throw new IllegalArgumentException("Flight arrival time must be after departure time");
        }
        if (status == null || aircraftId == null) throw new IllegalArgumentException("Flight status and aircraft are required");
    }
}
