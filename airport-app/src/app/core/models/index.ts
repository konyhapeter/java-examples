export enum FlightStatus {
  SCHEDULED = 'SCHEDULED',
  DELAYED = 'DELAYED',
  BOARDING = 'BOARDING',
  DEPARTED = 'DEPARTED',
  LANDED = 'LANDED',
  CANCELLED = 'CANCELLED'
}

export enum AircraftStatus {
  AVAILABLE = 'AVAILABLE',
  IN_USE = 'IN_USE',
  MAINTENANCE = 'MAINTENANCE',
  RETIRED = 'RETIRED'
}

export enum BookingStatus {
  CONFIRMED = 'CONFIRMED',
  CHECKED_IN = 'CHECKED_IN',
  CANCELLED = 'CANCELLED'
}

export interface Aircraft {
  id: number;
  registration: string;
  model: string;
  capacity: number;
  status: AircraftStatus;
}

export interface AircraftRequest {
  registration: string;
  model: string;
  capacity: number;
  status: AircraftStatus;
}

export interface Passenger {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  passportNumber: string;
}

export interface PassengerRequest {
  firstName: string;
  lastName: string;
  email: string;
  passportNumber: string;
}

export interface Flight {
  id: number;
  flightNumber: string;
  origin: string;
  destination: string;
  departureTime: Date;
  arrivalTime: Date;
  status: FlightStatus;
  aircraftId: number;
  aircraftRegistration: string;
}

export interface FlightRequest {
  flightNumber: string;
  origin: string;
  destination: string;
  departureTime: Date;
  arrivalTime: Date;
  status: FlightStatus;
  aircraftId: number;
}

export interface Booking {
  id: number;
  passengerId: number;
  passengerName: string;
  flightId: number;
  flightNumber: string;
  seatNumber: string;
  status: BookingStatus;
}

export interface BookingRequest {
  passengerId: number;
  flightId: number;
  seatNumber: string;
  status: BookingStatus;
}
