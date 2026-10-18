package org.example.airportmanagement.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "passengers")
public class PassengerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80)
    private String firstName;
    @Column(nullable = false, length = 80)
    private String lastName;
    @Column(nullable = false, unique = true, length = 254)
    private String email;
    @Column(nullable = false, unique = true, length = 30)
    private String passportNumber;

    protected PassengerEntity() { }

    public PassengerEntity(String firstName, String lastName, String email, String passportNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passportNumber = passportNumber;
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPassportNumber() { return passportNumber; }
    public void update(String firstName, String lastName, String email, String passportNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passportNumber = passportNumber;
    }
}
