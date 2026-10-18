package org.example.airportmanagement.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.example.airportmanagement.domain.BookingStatus;

@Entity
@Table(name = "bookings")
public class BookingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false)
    private PassengerEntity passenger;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false)
    private FlightEntity flight;
    @Column(name = "seat_number", nullable = false, length = 8)
    private String seatNumber;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    protected BookingEntity() { }

    public BookingEntity(PassengerEntity passenger, FlightEntity flight, String seatNumber, BookingStatus status) {
        update(passenger, flight, seatNumber, status);
    }

    public void update(PassengerEntity passenger, FlightEntity flight, String seatNumber, BookingStatus status) {
        this.passenger = passenger;
        this.flight = flight;
        this.seatNumber = seatNumber;
        this.status = status;
    }

    public Long getId() { return id; }
    public PassengerEntity getPassenger() { return passenger; }
    public FlightEntity getFlight() { return flight; }
    public String getSeatNumber() { return seatNumber; }
    public BookingStatus getStatus() { return status; }
}
