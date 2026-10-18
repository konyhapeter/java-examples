package org.example.airportmanagement.domain;

public record Aircraft(Long id, String registration, String model, int capacity, AircraftStatus status) {
    public Aircraft {
        if (registration == null || registration.isBlank()) throw new IllegalArgumentException("Registration is required");
        if (model == null || model.isBlank()) throw new IllegalArgumentException("Aircraft model is required");
        if (capacity < 1) throw new IllegalArgumentException("Aircraft capacity must be positive");
        if (status == null) throw new IllegalArgumentException("Aircraft status is required");
    }

    public Aircraft withId(Long id) {
        return new Aircraft(id, registration, model, capacity, status);
    }
}
