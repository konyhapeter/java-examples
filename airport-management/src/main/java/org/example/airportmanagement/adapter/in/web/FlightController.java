package org.example.airportmanagement.adapter.in.web;

import jakarta.validation.Valid;
import org.example.airportmanagement.api.ApiMapper;
import org.example.airportmanagement.api.ApiModels.FlightRequest;
import org.example.airportmanagement.api.ApiModels.FlightResponse;
import org.example.airportmanagement.application.service.FlightApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class FlightController {
    private final FlightApplicationService service;
    public FlightController(FlightApplicationService service) { this.service = service; }

    @GetMapping({"/api/flights", "/api/timetable"})
    public List<FlightResponse> findAll() { return service.findAll().stream().map(ApiMapper::toResponse).toList(); }
    @GetMapping("/api/flights/{id}")
    public FlightResponse findById(@PathVariable Long id) { return ApiMapper.toResponse(service.findById(id)); }
    @PostMapping("/api/flights")
    public ResponseEntity<FlightResponse> create(@Valid @RequestBody FlightRequest request) {
        FlightResponse response = ApiMapper.toResponse(service.create(ApiMapper.toCommand(request)));
        return ResponseEntity.created(URI.create("/api/flights/" + response.id())).body(response);
    }
    @PutMapping("/api/flights/{id}")
    public FlightResponse update(@PathVariable Long id, @Valid @RequestBody FlightRequest request) {
        return ApiMapper.toResponse(service.update(id, ApiMapper.toCommand(request)));
    }
    @DeleteMapping("/api/flights/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
