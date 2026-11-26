import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  Aircraft,
  AircraftRequest,
  Passenger,
  PassengerRequest,
  Flight,
  FlightRequest,
  Booking,
  BookingRequest
} from '../models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly apiUrl = '/api';

  constructor(private http: HttpClient) {}

  // Aircraft endpoints
  getAircraft(): Observable<Aircraft[]> {
    return this.http.get<Aircraft[]>(`${this.apiUrl}/aircraft`);
  }

  getAircraftById(id: number): Observable<Aircraft> {
    return this.http.get<Aircraft>(`${this.apiUrl}/aircraft/${id}`);
  }

  createAircraft(aircraft: AircraftRequest): Observable<Aircraft> {
    return this.http.post<Aircraft>(`${this.apiUrl}/aircraft`, aircraft);
  }

  updateAircraft(id: number, aircraft: AircraftRequest): Observable<Aircraft> {
    return this.http.put<Aircraft>(`${this.apiUrl}/aircraft/${id}`, aircraft);
  }

  deleteAircraft(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/aircraft/${id}`);
  }

  // Passenger endpoints
  getPassengers(): Observable<Passenger[]> {
    return this.http.get<Passenger[]>(`${this.apiUrl}/passengers`);
  }

  getPassengerById(id: number): Observable<Passenger> {
    return this.http.get<Passenger>(`${this.apiUrl}/passengers/${id}`);
  }

  createPassenger(passenger: PassengerRequest): Observable<Passenger> {
    return this.http.post<Passenger>(`${this.apiUrl}/passengers`, passenger);
  }

  updatePassenger(id: number, passenger: PassengerRequest): Observable<Passenger> {
    return this.http.put<Passenger>(`${this.apiUrl}/passengers/${id}`, passenger);
  }

  deletePassenger(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/passengers/${id}`);
  }

  // Flight endpoints
  getFlights(): Observable<Flight[]> {
    return this.http.get<Flight[]>(`${this.apiUrl}/flights`);
  }

  getFlightById(id: number): Observable<Flight> {
    return this.http.get<Flight>(`${this.apiUrl}/flights/${id}`);
  }

  createFlight(flight: FlightRequest): Observable<Flight> {
    return this.http.post<Flight>(`${this.apiUrl}/flights`, flight);
  }

  updateFlight(id: number, flight: FlightRequest): Observable<Flight> {
    return this.http.put<Flight>(`${this.apiUrl}/flights/${id}`, flight);
  }

  deleteFlight(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/flights/${id}`);
  }

  // Booking endpoints
  getBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.apiUrl}/bookings`);
  }

  getBookingById(id: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.apiUrl}/bookings/${id}`);
  }

  getBookingsByFlight(flightId: number): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.apiUrl}/bookings/flight/${flightId}`);
  }

  createBooking(booking: BookingRequest): Observable<Booking> {
    return this.http.post<Booking>(`${this.apiUrl}/bookings`, booking);
  }

  updateBooking(id: number, booking: BookingRequest): Observable<Booking> {
    return this.http.put<Booking>(`${this.apiUrl}/bookings/${id}`, booking);
  }

  deleteBooking(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/bookings/${id}`);
  }
}
