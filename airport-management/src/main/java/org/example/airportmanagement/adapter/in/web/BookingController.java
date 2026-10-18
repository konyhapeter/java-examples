package org.example.airportmanagement.adapter.in.web;

import jakarta.validation.Valid;
import org.example.airportmanagement.api.ApiMapper;
import org.example.airportmanagement.api.ApiModels.BookingRequest;
import org.example.airportmanagement.api.ApiModels.BookingResponse;
import org.example.airportmanagement.application.service.BookingApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingApplicationService service;
    public BookingController(BookingApplicationService service) { this.service = service; }

    @GetMapping
    public List<BookingResponse> findAll() { return service.findAll().stream().map(ApiMapper::toResponse).toList(); }
    @GetMapping("/{id}")
    public BookingResponse findById(@PathVariable Long id) { return ApiMapper.toResponse(service.findById(id)); }
    @GetMapping("/flight/{flightId}")
    public List<BookingResponse> findByFlight(@PathVariable Long flightId) {
        return service.findByFlightId(flightId).stream().map(ApiMapper::toResponse).toList();
    }
    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        BookingResponse response = ApiMapper.toResponse(service.create(ApiMapper.toCommand(request)));
        return ResponseEntity.created(URI.create("/api/bookings/" + response.id())).body(response);
    }
    @PutMapping("/{id}")
    public BookingResponse update(@PathVariable Long id, @Valid @RequestBody BookingRequest request) {
        return ApiMapper.toResponse(service.update(id, ApiMapper.toCommand(request)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
