package org.example.airportmanagement.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.example.airportmanagement.domain.FlightStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
public class FlightEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 12)
    private String flightNumber;
    @Column(nullable = false, length = 100)
    private String origin;
    @Column(nullable = false, length = 100)
    private String destination;
    @Column(nullable = false)
    private LocalDateTime departureTime;
    @Column(nullable = false)
    private LocalDateTime arrivalTime;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FlightStatus status;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private AircraftEntity aircraft;

    protected FlightEntity() { }

    public FlightEntity(String flightNumber, String origin, String destination, LocalDateTime departureTime,
                        LocalDateTime arrivalTime, FlightStatus status, AircraftEntity aircraft) {
        update(flightNumber, origin, destination, departureTime, arrivalTime, status, aircraft);
    }

    public void update(String flightNumber, String origin, String destination, LocalDateTime departureTime,
                       LocalDateTime arrivalTime, FlightStatus status, AircraftEntity aircraft) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
        this.aircraft = aircraft;
    }

    public Long getId() { return id; }
    public String getFlightNumber() { return flightNumber; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public FlightStatus getStatus() { return status; }
    public AircraftEntity getAircraft() { return aircraft; }
}
