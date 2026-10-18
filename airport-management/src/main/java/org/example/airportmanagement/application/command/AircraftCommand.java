package org.example.airportmanagement.application.command;

import org.example.airportmanagement.domain.AircraftStatus;

public record AircraftCommand(String registration, String model, int capacity, AircraftStatus status) {
}
