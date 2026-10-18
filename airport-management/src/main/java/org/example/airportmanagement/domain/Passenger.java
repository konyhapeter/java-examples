package org.example.airportmanagement.domain;

public record Passenger(Long id, String firstName, String lastName, String email, String passportNumber) {
    public Passenger {
        if (firstName == null || firstName.isBlank()) throw new IllegalArgumentException("First name is required");
        if (lastName == null || lastName.isBlank()) throw new IllegalArgumentException("Last name is required");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required");
        if (passportNumber == null || passportNumber.isBlank()) throw new IllegalArgumentException("Passport number is required");
    }

    public Passenger withId(Long id) {
        return new Passenger(id, firstName, lastName, email, passportNumber);
    }
}
