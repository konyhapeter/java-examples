package org.example.airportmanagement.api;

import org.example.airportmanagement.domain.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public final class ApiModels {
    private ApiModels() {
    }

    public record AircraftRequest(
            @NotBlank @Size(max = 20) String registration,
            @NotBlank @Size(max = 100) String model,
            @Positive int capacity,
            @NotNull AircraftStatus status) {
    }

    public record AircraftResponse(Long id, String registration, String model, int capacity, AircraftStatus status) {
    }

    public record PassengerRequest(
            @NotBlank @Size(max = 80) String firstName,
            @NotBlank @Size(max = 80) String lastName,
            @NotBlank @Email @Size(max = 254) String email,
            @NotBlank @Size(max = 30) String passportNumber) {
    }

    public record PassengerResponse(Long id, String firstName, String lastName, String email, String passportNumber) {
    }

    public record FlightRequest(
            @NotBlank @Size(max = 12) String flightNumber,
            @NotBlank @Size(max = 100) String origin,
            @NotBlank @Size(max = 100) String destination,
            @NotNull LocalDateTime departureTime,
            @NotNull LocalDateTime arrivalTime,
            @NotNull FlightStatus status,
            @NotNull @Positive Long aircraftId) {
    }

    public record FlightResponse(
            Long id,
            String flightNumber,
            String origin,
            String destination,
            LocalDateTime departureTime,
            LocalDateTime arrivalTime,
            FlightStatus status,
            Long aircraftId,
            String aircraftRegistration) {
    }

    public record BookingRequest(
            @NotNull @Positive Long passengerId,
            @NotNull @Positive Long flightId,
            @NotBlank @Size(max = 8) String seatNumber,
            @NotNull BookingStatus status) {
    }

    public record BookingResponse(
            Long id,
            Long passengerId,
            String passengerName,
            Long flightId,
            String flightNumber,
            String seatNumber,
            BookingStatus status) {
    }

    public record ApiError(String message, LocalDateTime timestamp) {
    }
}
