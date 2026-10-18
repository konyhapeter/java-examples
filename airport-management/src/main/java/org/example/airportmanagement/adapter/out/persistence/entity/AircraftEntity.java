package org.example.airportmanagement.adapter.out.persistence.entity;

import jakarta.persistence.*;
import org.example.airportmanagement.domain.AircraftStatus;

@Entity
@Table(name = "aircraft")
public class AircraftEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 20)
    private String registration;
    @Column(nullable = false, length = 100)
    private String model;
    @Column(nullable = false)
    private int capacity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AircraftStatus status;

    protected AircraftEntity() { }

    public AircraftEntity(String registration, String model, int capacity, AircraftStatus status) {
        this.registration = registration;
        this.model = model;
        this.capacity = capacity;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getRegistration() { return registration; }
    public String getModel() { return model; }
    public int getCapacity() { return capacity; }
    public AircraftStatus getStatus() { return status; }
    public void update(String registration, String model, int capacity, AircraftStatus status) {
        this.registration = registration;
        this.model = model;
        this.capacity = capacity;
        this.status = status;
    }
}
