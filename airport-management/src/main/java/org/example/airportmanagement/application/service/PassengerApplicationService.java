package org.example.airportmanagement.application.service;

import org.example.airportmanagement.application.command.PassengerCommand;
import org.example.airportmanagement.application.exception.ResourceNotFoundException;
import org.example.airportmanagement.application.port.out.PassengerStore;
import org.example.airportmanagement.domain.Passenger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PassengerApplicationService {
    private final PassengerStore passengers;

    public PassengerApplicationService(PassengerStore passengers) { this.passengers = passengers; }

    @Transactional(readOnly = true)
    public List<Passenger> findAll() { return passengers.findAll(); }

    @Transactional(readOnly = true)
    public Passenger findById(Long id) { return passengers.findById(id).orElseThrow(() -> new ResourceNotFoundException("Passenger", id)); }

    public Passenger create(PassengerCommand command) {
        return passengers.save(new Passenger(null, command.firstName(), command.lastName(), command.email(), command.passportNumber()));
    }

    public Passenger update(Long id, PassengerCommand command) {
        findById(id);
        return passengers.save(new Passenger(id, command.firstName(), command.lastName(), command.email(), command.passportNumber()));
    }

    public void delete(Long id) { passengers.delete(findById(id)); }
}
