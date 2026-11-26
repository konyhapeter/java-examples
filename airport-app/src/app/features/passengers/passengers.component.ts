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
  ],
  template: `
    <div class="container">
      <div class="header">
        <h2>Passengers Management</h2>
        <button (click)="toggleForm()" class="btn-primary">
          {{ showForm ? 'Cancel' : '+ New Passenger' }}
        </button>
      </div>

      <app-loading [isLoading]="loading" message="Loading passengers..."></app-loading>

      <div *ngIf="showForm" class="form-section">
        <form [formGroup]="passengerForm" (ngSubmit)="submitForm()">
          <div class="form-row">
            <div class="form-group">
              <label>First Name *</label>
              <input type="text" formControlName="firstName" class="input" />
            </div>
            <div class="form-group">
              <label>Last Name *</label>
              <input type="text" formControlName="lastName" class="input" />
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Email *</label>
              <input type="email" formControlName="email" class="input" />
            </div>
            <div class="form-group">
              <label>Passport Number *</label>
              <input type="text" formControlName="passportNumber" class="input" />
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-primary" [disabled]="!passengerForm.valid">
              {{ editingId ? 'Update Passenger' : 'Create Passenger' }}
            </button>
            <button type="button" (click)="resetForm()" class="btn-secondary">
              Reset
            </button>
          </div>
        </form>
      </div>

      <div *ngIf="!loading && passengers.length > 0" class="table-section">
        <table class="table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Email</th>
              <th>Passport</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let p of passengers">
              <td><strong>{{ p.firstName }} {{ p.lastName }}</strong></td>
              <td>{{ p.email }}</td>
              <td>{{ p.passportNumber }}</td>
              <td>
                <button (click)="editPassenger(p)" class="btn-edit">Edit</button>
                <button (click)="deletePassenger(p.id)" class="btn-delete">Delete</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div *ngIf="!loading && passengers.length === 0" class="empty-state">
        <p>No passengers found. Add one to get started!</p>
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
