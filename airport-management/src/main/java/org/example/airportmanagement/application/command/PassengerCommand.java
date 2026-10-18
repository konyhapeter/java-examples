package org.example.airportmanagement.application.command;

public record PassengerCommand(String firstName, String lastName, String email, String passportNumber) {
}
