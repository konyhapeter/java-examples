package org.example.airportmanagement.application.command;

import org.example.airportmanagement.domain.BookingStatus;

public record BookingCommand(Long passengerId, Long flightId, String seatNumber, BookingStatus status) {
}
