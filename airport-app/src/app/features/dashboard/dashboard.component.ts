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
  ],
  template: `
    <div class="container">
      <h1>Dashboard</h1>

      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-value">{{ flightCount }}</div>
          <div class="stat-label">Total Flights</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ aircraftCount }}</div>
          <div class="stat-label">Aircraft</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ passengerCount }}</div>
          <div class="stat-label">Passengers</div>
        </div>
        <div class="stat-card">
          <div class="stat-value">{{ bookingCount }}</div>
          <div class="stat-label">Bookings</div>
        </div>
      </div>

      <div class="content-grid">
        <div class="card">
          <h3>Upcoming Flights</h3>
          <app-loading [isLoading]="loading" message="Loading flights..."></app-loading>
          <div *ngIf="!loading && upcomingFlights.length > 0" class="flight-list">
            <div *ngFor="let flight of upcomingFlights.slice(0, 5)" class="flight-item">
              <div class="flight-info">
                <strong>{{ flight.flightNumber }}</strong>
                <span class="route">{{ flight.origin }} → {{ flight.destination }}</span>
              </div>
              <div class="flight-time">
                {{ flight.departureTime | dateFormat }}
              </div>
              <app-status-badge [status]="flight.status"></app-status-badge>
            </div>
          </div>
          <div *ngIf="!loading && upcomingFlights.length === 0" class="empty">
            No upcoming flights
          </div>
        </div>

        <div class="card">
          <h3>Aircraft Status</h3>
          <app-loading [isLoading]="loading" message="Loading aircraft..."></app-loading>
          <div *ngIf="!loading && aircraft.length > 0" class="status-breakdown">
            <div class="status-item" *ngFor="let status of aircraftStatuses">
              <span class="label">{{ status }}</span>
              <span class="count">{{ getAircraftCount(status) }}</span>
            </div>
          </div>
          <div *ngIf="!loading && aircraft.length === 0" class="empty">
            No aircraft available
          </div>
        </div>

        <div class="card">
          <h3>Booking Status</h3>
          <app-loading [isLoading]="loading" message="Loading bookings..."></app-loading>
          <div *ngIf="!loading && bookings.length > 0" class="status-breakdown">
            <div class="status-item" *ngFor="let status of bookingStatuses">
              <span class="label">{{ status }}</span>
              <span class="count">{{ getBookingCount(status) }}</span>
            </div>
          </div>
          <div *ngIf="!loading && bookings.length === 0" class="empty">
            No bookings available
          </div>
        </div>

        <div class="card">
          <h3>Recent Bookings</h3>
          <app-loading [isLoading]="loading" message="Loading bookings..."></app-loading>
          <div *ngIf="!loading && bookings.length > 0" class="booking-list">
            <div *ngFor="let booking of bookings.slice(0, 5)" class="booking-item">
              <div class="booking-info">
                <strong>{{ booking.passengerName }}</strong>
                <span class="flight">Flight {{ booking.flightNumber }}</span>
                <span class="seat">Seat {{ booking.seatNumber }}</span>
              </div>
              <app-status-badge [status]="booking.status"></app-status-badge>
            </div>
          </div>
          <div *ngIf="!loading && bookings.length === 0" class="empty">
            No bookings available
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .container {
      max-width: 1400px;
      margin: 0 auto;
      padding: 2rem;
    }

    h1 {
      color: #333;
      margin-bottom: 2rem;
      font-size: 2.5rem;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 1.5rem;
      margin-bottom: 3rem;
    }

    .stat-card {
      background: linear-gradient(135deg, #1a5490 0%, #0d3a6f 100%);
      color: white;
      padding: 2rem;
      border-radius: 8px;
      text-align: center;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      transition: transform 0.3s;

      &:hover {
        transform: translateY(-4px);
      }
    }

    .stat-value {
      font-size: 2.5rem;
      font-weight: bold;
      margin-bottom: 0.5rem;
    }

    .stat-label {
      font-size: 0.95rem;
      opacity: 0.9;
    }

    .content-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      gap: 2rem;
    }

    .card {
      background: white;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }

    .card h3 {
      margin: 0 0 1rem 0;
      color: #333;
      font-size: 1.3rem;
      border-bottom: 2px solid #1a5490;
      padding-bottom: 0.5rem;
    }

    .empty {
      text-align: center;
      padding: 2rem;
      color: #999;
      font-size: 0.95rem;
    }

    .flight-list {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .flight-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 1rem;
      background: #f9f9f9;
      border-radius: 4px;
      border-left: 4px solid #1a5490;
      transition: background 0.3s;

      &:hover {
        background: #f0f0f0;
      }
    }

    .flight-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 0.3rem;
    }

    .flight-info strong {
      color: #333;
      font-size: 1rem;
    }

    .route {
      color: #666;
      font-size: 0.85rem;
    }

    .flight-time {
      color: #666;
      font-size: 0.9rem;
      margin-right: 1rem;
      min-width: 180px;
      text-align: right;
    }

    .status-breakdown {
      display: flex;
      flex-direction: column;
      gap: 0.8rem;
    }

    .status-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 0.8rem;
      background: #f9f9f9;
      border-radius: 4px;
    }

    .status-item .label {
      color: #333;
      font-weight: 500;
    }

    .status-item .count {
      background: #1a5490;
      color: white;
      padding: 0.3rem 0.8rem;
      border-radius: 20px;
      font-weight: bold;
      font-size: 0.95rem;
    }

    .booking-list {
      display: flex;
      flex-direction: column;
      gap: 1rem;
    }

    .booking-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 1rem;
      background: #f9f9f9;
      border-radius: 4px;
      border-left: 4px solid #4caf50;
    }

    .booking-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 0.3rem;
    }

    .booking-info strong {
      color: #333;
      font-size: 1rem;
    }

    .flight {
      color: #666;
      font-size: 0.85rem;
    }

    .seat {
      color: #999;
      font-size: 0.8rem;
    }

    @media (max-width: 768px) {
      .stats-grid {
        grid-template-columns: 1fr 1fr;
      }

      .content-grid {
        grid-template-columns: 1fr;
      }

      .flight-item,
      .booking-item {
        flex-direction: column;
        align-items: flex-start;
      }

      .flight-time {
        width: 100%;
        text-align: left;
        margin-right: 0;
        margin-top: 0.5rem;
      }
    }
  `]
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
