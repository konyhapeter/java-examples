package org.example.airportmanagement.application.command;

import org.example.airportmanagement.domain.FlightStatus;

import java.time.LocalDateTime;

public record FlightCommand(String flightNumber, String origin, String destination, LocalDateTime departureTime,
                            LocalDateTime arrivalTime, FlightStatus status, Long aircraftId) {
}
