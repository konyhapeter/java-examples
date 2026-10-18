package org.example.airportmanagement.adapter.out.persistence.repository;

import org.example.airportmanagement.adapter.out.persistence.entity.FlightEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FlightJpaRepository extends JpaRepository<FlightEntity, Long> {
    List<FlightEntity> findByAircraftId(Long aircraftId);

    @Query("select f.aircraft.capacity from FlightEntity f where f.id = :flightId")
    Integer capacityForFlight(@Param("flightId") Long flightId);
}
