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
  ],  templateUrl: './flights.component.html',  styleUrl: './flights.component.scss'
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
