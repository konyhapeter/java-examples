package org.example.airportmanagement.adapter.in.web;

import jakarta.validation.Valid;
import org.example.airportmanagement.api.ApiMapper;
import org.example.airportmanagement.api.ApiModels.AircraftRequest;
import org.example.airportmanagement.api.ApiModels.AircraftResponse;
import org.example.airportmanagement.application.service.AircraftApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/aircraft")
public class AircraftController {
    private final AircraftApplicationService service;
    public AircraftController(AircraftApplicationService service) { this.service = service; }

    @GetMapping
    public List<AircraftResponse> findAll() { return service.findAll().stream().map(ApiMapper::toResponse).toList(); }
    @GetMapping("/{id}")
    public AircraftResponse findById(@PathVariable Long id) { return ApiMapper.toResponse(service.findById(id)); }
    @PostMapping
    public ResponseEntity<AircraftResponse> create(@Valid @RequestBody AircraftRequest request) {
        AircraftResponse response = ApiMapper.toResponse(service.create(ApiMapper.toCommand(request)));
        return ResponseEntity.created(URI.create("/api/aircraft/" + response.id())).body(response);
    }
    @PutMapping("/{id}")
    public AircraftResponse update(@PathVariable Long id, @Valid @RequestBody AircraftRequest request) {
        return ApiMapper.toResponse(service.update(id, ApiMapper.toCommand(request)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
