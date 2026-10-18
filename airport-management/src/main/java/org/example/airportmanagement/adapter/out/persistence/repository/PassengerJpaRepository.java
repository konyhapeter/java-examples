package org.example.airportmanagement.adapter.out.persistence.repository;

import org.example.airportmanagement.adapter.out.persistence.entity.PassengerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerJpaRepository extends JpaRepository<PassengerEntity, Long> {
}
