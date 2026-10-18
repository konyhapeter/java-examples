package org.example.airportmanagement.adapter.out.persistence.repository;

import org.example.airportmanagement.adapter.out.persistence.entity.AircraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AircraftJpaRepository extends JpaRepository<AircraftEntity, Long> {
}
