package org.example.airportmanagement.adapter.in.web;

import jakarta.validation.Valid;
import org.example.airportmanagement.api.ApiMapper;
import org.example.airportmanagement.api.ApiModels.PassengerRequest;
import org.example.airportmanagement.api.ApiModels.PassengerResponse;
import org.example.airportmanagement.application.service.PassengerApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/passengers")
public class PassengerController {
    private final PassengerApplicationService service;
    public PassengerController(PassengerApplicationService service) { this.service = service; }

    @GetMapping
    public List<PassengerResponse> findAll() { return service.findAll().stream().map(ApiMapper::toResponse).toList(); }
    @GetMapping("/{id}")
    public PassengerResponse findById(@PathVariable Long id) { return ApiMapper.toResponse(service.findById(id)); }
    @PostMapping
    public ResponseEntity<PassengerResponse> create(@Valid @RequestBody PassengerRequest request) {
        PassengerResponse response = ApiMapper.toResponse(service.create(ApiMapper.toCommand(request)));
        return ResponseEntity.created(URI.create("/api/passengers/" + response.id())).body(response);
    }
    @PutMapping("/{id}")
    public PassengerResponse update(@PathVariable Long id, @Valid @RequestBody PassengerRequest request) {
        return ApiMapper.toResponse(service.update(id, ApiMapper.toCommand(request)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
