import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '@core/services/api.service';
import { Booking, BookingRequest, BookingStatus } from '@core/models';
import { LoadingComponent } from '@shared/components/loading.component';
import { StatusBadgeComponent } from '@shared/components/status-badge.component';

@Component({
  selector: 'app-bookings',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    LoadingComponent,
    StatusBadgeComponent
  ],  templateUrl: './bookings.component.html',  styleUrl: './bookings.component.scss'
})
export class BookingsComponent implements OnInit {
  bookings: Booking[] = [];
  loading = false;
  showForm = false;
  editingId: number | null = null;

  bookingStatuses = Object.values(BookingStatus);
  bookingForm: FormGroup;

  constructor(
    private apiService: ApiService,
    private fb: FormBuilder
  ) {
    this.bookingForm = this.fb.group({
      passengerId: ['', Validators.required],
      flightId: ['', Validators.required],
      seatNumber: ['', [Validators.required, Validators.maxLength(8)]],
      status: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadBookings();
  }

  loadBookings(): void {
    this.loading = true;
    this.apiService.getBookings().subscribe({
      next: (data) => {
        this.bookings = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading bookings:', err);
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
    if (!this.bookingForm.valid) return;

    const request: BookingRequest = this.bookingForm.value;

    if (this.editingId) {
      this.apiService.updateBooking(this.editingId, request).subscribe({
        next: () => {
          this.loadBookings();
          this.resetForm();
        },
        error: (err) => console.error('Error updating booking:', err)
      });
    } else {
      this.apiService.createBooking(request).subscribe({
        next: () => {
          this.loadBookings();
          this.resetForm();
        },
        error: (err) => console.error('Error creating booking:', err)
      });
    }
  }

  editBooking(b: Booking): void {
    this.editingId = b.id;
    this.bookingForm.patchValue({
      passengerId: b.passengerId,
      flightId: b.flightId,
      seatNumber: b.seatNumber,
      status: b.status
    });
    this.showForm = true;
  }

  deleteBooking(id: number): void {
    if (confirm('Are you sure you want to delete this booking?')) {
      this.apiService.deleteBooking(id).subscribe({
        next: () => this.loadBookings(),
        error: (err) => console.error('Error deleting booking:', err)
      });
    }
  }

  resetForm(): void {
    this.bookingForm.reset();
    this.editingId = null;
    this.showForm = false;
  }
}
