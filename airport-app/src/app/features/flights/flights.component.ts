import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '@core/services/api.service';
import { Flight, FlightRequest, FlightStatus, AircraftStatus } from '@core/models';
import { LoadingComponent } from '@shared/components/loading.component';
import { StatusBadgeComponent } from '@shared/components/status-badge.component';
import { DateFormatPipe } from '@shared/pipes/date-format.pipe';

@Component({
  selector: 'app-flights',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    LoadingComponent,
    StatusBadgeComponent,
    DateFormatPipe
  ],
  template: `
    <div class="container">
      <div class="header">
        <h2>Flights Management</h2>
        <button (click)="toggleForm()" class="btn-primary">
          {{ showForm ? 'Cancel' : '+ New Flight' }}
        </button>
      </div>

      <app-loading [isLoading]="loading" message="Loading flights..."></app-loading>

      <div *ngIf="showForm" class="form-section">
        <form [formGroup]="flightForm" (ngSubmit)="submitForm()">
          <div class="form-row">
            <div class="form-group">
              <label>Flight Number *</label>
              <input type="text" formControlName="flightNumber" class="input" />
            </div>
            <div class="form-group">
              <label>Origin *</label>
              <input type="text" formControlName="origin" class="input" />
            </div>
            <div class="form-group">
              <label>Destination *</label>
              <input type="text" formControlName="destination" class="input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Departure Time *</label>
              <input type="datetime-local" formControlName="departureTime" class="input" />
            </div>
            <div class="form-group">
              <label>Arrival Time *</label>
              <input type="datetime-local" formControlName="arrivalTime" class="input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Status *</label>
              <select formControlName="status" class="input">
                <option value="">Select Status</option>
                <option *ngFor="let status of flightStatuses" [value]="status">
                  {{ status }}
                </option>
              </select>
            </div>
            <div class="form-group">
              <label>Aircraft ID *</label>
              <input type="number" formControlName="aircraftId" class="input" />
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-primary" [disabled]="!flightForm.valid">
              {{ editingId ? 'Update Flight' : 'Create Flight' }}
            </button>
            <button type="button" (click)="resetForm()" class="btn-secondary">
              Reset
            </button>
          </div>
        </form>
      </div>

      <div *ngIf="!loading && flights.length > 0" class="table-section">
        <table class="table">
          <thead>
            <tr>
              <th>Flight #</th>
              <th>Route</th>
              <th>Aircraft</th>
              <th>Departure</th>
              <th>Arrival</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let flight of flights">
              <td><strong>{{ flight.flightNumber }}</strong></td>
              <td>{{ flight.origin }} → {{ flight.destination }}</td>
              <td>{{ flight.aircraftRegistration }}</td>
              <td>{{ flight.departureTime | dateFormat }}</td>
              <td>{{ flight.arrivalTime | dateFormat }}</td>
              <td><app-status-badge [status]="flight.status"></app-status-badge></td>
              <td>
                <button (click)="editFlight(flight)" class="btn-edit">Edit</button>
                <button (click)="deleteFlight(flight.id)" class="btn-delete">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div *ngIf="!loading && flights.length === 0" class="empty-state">
        <p>No flights found. Create one to get started!</p>
      </div>
    </div>
  `,
  styles: [`
    .container {
      max-width: 1400px;
      margin: 0 auto;
      padding: 2rem;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
      border-bottom: 2px solid #e0e0e0;
      padding-bottom: 1rem;
    }

    h2 {
      margin: 0;
      color: #333;
    }

    .btn-primary {
      background-color: #1a5490;
      color: white;
      border: none;
      padding: 0.6rem 1.2rem;
      border-radius: 4px;
      cursor: pointer;
      font-weight: 500;
      transition: background-color 0.3s;

      &:hover:not(:disabled) {
        background-color: #0d3a6f;
      }

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }

    .btn-secondary {
      background-color: #757575;
      color: white;
      border: none;
      padding: 0.6rem 1.2rem;
      border-radius: 4px;
      cursor: pointer;
      transition: background-color 0.3s;

      &:hover {
        background-color: #616161;
      }
    }

    .btn-edit, .btn-delete {
      padding: 0.4rem 0.8rem;
      margin-right: 0.5rem;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 0.85rem;
      transition: all 0.3s;
    }

    .btn-edit {
      background-color: #2196f3;
      color: white;

      &:hover {
        background-color: #1976d2;
      }
    }

    .btn-delete {
      background-color: #f44336;
      color: white;

      &:hover {
        background-color: #da190b;
      }
    }

    .form-section {
      background-color: #f5f5f5;
      padding: 1.5rem;
      border-radius: 8px;
      margin-bottom: 2rem;
      border: 1px solid #ddd;
    }

    .form-row {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1rem;
      margin-bottom: 1rem;
    }

    .form-group {
      display: flex;
      flex-direction: column;
    }

    label {
      font-weight: 600;
      margin-bottom: 0.5rem;
      color: #333;
      font-size: 0.95rem;
    }

    .input {
      padding: 0.6rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 0.95rem;
      font-family: inherit;

      &:focus {
        outline: none;
        border-color: #1a5490;
        box-shadow: 0 0 0 3px rgba(26, 84, 144, 0.1);
      }
    }

    .form-actions {
      display: flex;
      gap: 1rem;
      margin-top: 1.5rem;
    }

    .table-section {
      overflow-x: auto;
    }

    .table {
      width: 100%;
      border-collapse: collapse;
      background: white;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
      border-radius: 8px;
      overflow: hidden;
    }

    thead {
      background-color: #f5f5f5;
      border-bottom: 2px solid #ddd;
    }

    th {
      padding: 1rem;
      text-align: left;
      font-weight: 600;
      color: #333;
      font-size: 0.95rem;
    }

    td {
      padding: 1rem;
      border-bottom: 1px solid #eee;
      color: #666;
    }

    tbody tr:hover {
      background-color: #fafafa;
    }

    .empty-state {
      text-align: center;
      padding: 3rem;
      color: #999;
    }
  `]
})
export class FlightsComponent implements OnInit {
  flights: Flight[] = [];
  loading = false;
  showForm = false;
  editingId: number | null = null;

  flightStatuses = Object.values(FlightStatus);
  flightForm: FormGroup;

  constructor(
    private apiService: ApiService,
    private fb: FormBuilder
  ) {
    this.flightForm = this.fb.group({
      flightNumber: ['', Validators.required],
      origin: ['', Validators.required],
      destination: ['', Validators.required],
      departureTime: ['', Validators.required],
      arrivalTime: ['', Validators.required],
      status: ['', Validators.required],
      aircraftId: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadFlights();
  }

  loadFlights(): void {
    this.loading = true;
    this.apiService.getFlights().subscribe({
      next: (data) => {
        this.flights = data.map(f => ({
          ...f,
          departureTime: new Date(f.departureTime),
          arrivalTime: new Date(f.arrivalTime)
        }));
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading flights:', err);
        this.loading = false;
      }
    });
  }

  toggleForm(): void {
    this.showForm = !this.showForm;
    if (!this.showForm) {
      this.resetForm();
    }
  }

  submitForm(): void {
    if (!this.flightForm.valid) return;

    const formValue = this.flightForm.value;
    const request: FlightRequest = {
      ...formValue,
      departureTime: new Date(formValue.departureTime),
      arrivalTime: new Date(formValue.arrivalTime)
    };

    if (this.editingId) {
      this.apiService.updateFlight(this.editingId, request).subscribe({
        next: () => {
          this.loadFlights();
          this.resetForm();
        },
        error: (err) => console.error('Error updating flight:', err)
      });
    } else {
      this.apiService.createFlight(request).subscribe({
        next: () => {
          this.loadFlights();
          this.resetForm();
        },
        error: (err) => console.error('Error creating flight:', err)
      });
    }
  }

  editFlight(flight: Flight): void {
    this.editingId = flight.id;
    this.flightForm.patchValue({
      flightNumber: flight.flightNumber,
      origin: flight.origin,
      destination: flight.destination,
      departureTime: this.formatDateTimeLocal(flight.departureTime),
      arrivalTime: this.formatDateTimeLocal(flight.arrivalTime),
      status: flight.status,
      aircraftId: flight.aircraftId
    });
    this.showForm = true;
  }

  deleteFlight(id: number): void {
    if (confirm('Are you sure you want to delete this flight?')) {
      this.apiService.deleteFlight(id).subscribe({
        next: () => this.loadFlights(),
        error: (err) => console.error('Error deleting flight:', err)
      });
    }
  }

  resetForm(): void {
    this.flightForm.reset();
    this.editingId = null;
    this.showForm = false;
  }

  private formatDateTimeLocal(date: Date): string {
    if (!date) return '';
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }
}
