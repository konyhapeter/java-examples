import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '@core/services/api.service';
import { Passenger, PassengerRequest } from '@core/models';
import { LoadingComponent } from '@shared/components/loading.component';

@Component({
  selector: 'app-passengers',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    LoadingComponent
  ],  templateUrl: './passengers.component.html',  styleUrl: './passengers.component.scss'
})
export class PassengersComponent implements OnInit {
  passengers: Passenger[] = [];
  loading = false;
  showForm = false;
  editingId: number | null = null;

  passengerForm: FormGroup;

  constructor(
    private apiService: ApiService,
    private fb: FormBuilder
  ) {
    this.passengerForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.maxLength(80)]],
      lastName: ['', [Validators.required, Validators.maxLength(80)]],
      email: ['', [Validators.required, Validators.email]],
      passportNumber: ['', [Validators.required, Validators.maxLength(30)]]
    });
  }

  ngOnInit(): void {
    this.loadPassengers();
  }

  loadPassengers(): void {
    this.loading = true;
    this.apiService.getPassengers().subscribe({
      next: (data) => {
        this.passengers = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading passengers:', err);
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
    if (!this.passengerForm.valid) return;

    const request: PassengerRequest = this.passengerForm.value;

    if (this.editingId) {
      this.apiService.updatePassenger(this.editingId, request).subscribe({
        next: () => {
          this.loadPassengers();
          this.resetForm();
        },
        error: (err) => console.error('Error updating passenger:', err)
      });
    } else {
      this.apiService.createPassenger(request).subscribe({
        next: () => {
          this.loadPassengers();
          this.resetForm();
        },
        error: (err) => console.error('Error creating passenger:', err)
      });
    }
  }

  editPassenger(p: Passenger): void {
    this.editingId = p.id;
    this.passengerForm.patchValue(p);
    this.showForm = true;
  }

  deletePassenger(id: number): void {
    if (confirm('Are you sure you want to delete this passenger?')) {
      this.apiService.deletePassenger(id).subscribe({
        next: () => this.loadPassengers(),
        error: (err) => console.error('Error deleting passenger:', err)
      });
    }
  }

  resetForm(): void {
    this.passengerForm.reset();
    this.editingId = null;
    this.showForm = false;
  }
}
