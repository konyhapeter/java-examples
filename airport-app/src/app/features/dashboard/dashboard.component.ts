import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '@core/services/api.service';
import { Flight, Aircraft, Passenger, Booking, FlightStatus, AircraftStatus, BookingStatus } from '@core/models';
import { LoadingComponent } from '@shared/components/loading.component';
import { StatusBadgeComponent } from '@shared/components/status-badge.component';
import { DateFormatPipe } from '@shared/pipes/date-format.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    LoadingComponent,
    StatusBadgeComponent,
    DateFormatPipe
  ],  templateUrl: './dashboard.component.html',  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  flights: Flight[] = [];
  aircraft: Aircraft[] = [];
  passengers: Passenger[] = [];
  bookings: Booking[] = [];
  loading = false;

  aircraftStatuses = Object.values(AircraftStatus);
  bookingStatuses = Object.values(BookingStatus);

  get flightCount(): number {
    return this.flights.length;
  }

  get aircraftCount(): number {
    return this.aircraft.length;
  }

  get passengerCount(): number {
    return this.passengers.length;
  }

  get bookingCount(): number {
    return this.bookings.length;
  }

  get upcomingFlights(): Flight[] {
    return this.flights
      .filter(f => new Date(f.departureTime) > new Date())
      .sort((a, b) => new Date(a.departureTime).getTime() - new Date(b.departureTime).getTime());
  }

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading = true;

    Promise.all([
      this.loadFlights(),
      this.loadAircraft(),
      this.loadPassengers(),
      this.loadBookings()
    ]).then(() => {
      this.loading = false;
    }).catch(err => {
      console.error('Error loading dashboard data:', err);
      this.loading = false;
    });
  }

  private loadFlights(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.apiService.getFlights().subscribe({
        next: (data) => {
          this.flights = data.map(f => ({
            ...f,
            departureTime: new Date(f.departureTime),
            arrivalTime: new Date(f.arrivalTime)
          }));
          resolve();
        },
        error: reject
      });
    });
  }

  private loadAircraft(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.apiService.getAircraft().subscribe({
        next: (data) => {
          this.aircraft = data;
          resolve();
        },
        error: reject
      });
    });
  }

  private loadPassengers(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.apiService.getPassengers().subscribe({
        next: (data) => {
          this.passengers = data;
          resolve();
        },
        error: reject
      });
    });
  }

  private loadBookings(): Promise<void> {
    return new Promise((resolve, reject) => {
      this.apiService.getBookings().subscribe({
        next: (data) => {
          this.bookings = data;
          resolve();
        },
        error: reject
      });
    });
  }

  getAircraftCount(status: AircraftStatus): number {
    return this.aircraft.filter(a => a.status === status).length;
  }

  getBookingCount(status: BookingStatus): number {
    return this.bookings.filter(b => b.status === status).length;
  }
}
