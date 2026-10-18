package org.example.airportmanagement.api;

import org.example.airportmanagement.application.command.*;
import org.example.airportmanagement.domain.*;

import static org.example.airportmanagement.api.ApiModels.*;

public final class ApiMapper {
    private ApiMapper() { }

    public static AircraftCommand toCommand(AircraftRequest request) {
        return new AircraftCommand(request.registration(), request.model(), request.capacity(), request.status());
    }

    public static PassengerCommand toCommand(PassengerRequest request) {
        return new PassengerCommand(request.firstName(), request.lastName(), request.email(), request.passportNumber());
    }

    public static FlightCommand toCommand(FlightRequest request) {
        return new FlightCommand(request.flightNumber(), request.origin(), request.destination(),
                request.departureTime(), request.arrivalTime(), request.status(), request.aircraftId());
    }

    public static BookingCommand toCommand(BookingRequest request) {
        return new BookingCommand(request.passengerId(), request.flightId(), request.seatNumber(), request.status());
    }

    public static AircraftResponse toResponse(Aircraft aircraft) {
        return new AircraftResponse(aircraft.id(), aircraft.registration(), aircraft.model(), aircraft.capacity(), aircraft.status());
    }

    public static PassengerResponse toResponse(Passenger passenger) {
        return new PassengerResponse(passenger.id(), passenger.firstName(), passenger.lastName(),
                passenger.email(), passenger.passportNumber());
    }

    public static FlightResponse toResponse(Flight flight) {
        return new FlightResponse(flight.id(), flight.flightNumber(), flight.origin(), flight.destination(),
                flight.departureTime(), flight.arrivalTime(), flight.status(), flight.aircraftId(), flight.aircraftRegistration());
    }

    public static BookingResponse toResponse(Booking booking) {
        return new BookingResponse(booking.id(), booking.passengerId(), booking.passengerName(),
                booking.flightId(), booking.flightNumber(), booking.seatNumber(), booking.status());
    }
}
