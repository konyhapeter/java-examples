import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '@core/services/api.service';
import { Aircraft, AircraftRequest, AircraftStatus } from '@core/models';
import { LoadingComponent } from '@shared/components/loading.component';
import { StatusBadgeComponent } from '@shared/components/status-badge.component';

@Component({
  selector: 'app-aircraft',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    LoadingComponent,
    StatusBadgeComponent
  ],  templateUrl: './aircraft.component.html',  styleUrl: './aircraft.component.scss'
})
export class AircraftComponent implements OnInit {
  aircraft: Aircraft[] = [];
  loading = false;
  showForm = false;
  editingId: number | null = null;

  aircraftStatuses = Object.values(AircraftStatus);
  aircraftForm: FormGroup;

  constructor(
    private apiService: ApiService,
    private fb: FormBuilder
  ) {
    this.aircraftForm = this.fb.group({
      registration: ['', [Validators.required, Validators.maxLength(20)]],
      model: ['', [Validators.required, Validators.maxLength(100)]],
      capacity: ['', [Validators.required, Validators.min(1)]],
      status: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadAircraft();
  }

  loadAircraft(): void {
    this.loading = true;
    this.apiService.getAircraft().subscribe({
      next: (data) => {
        this.aircraft = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading aircraft:', err);
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
    if (!this.aircraftForm.valid) return;

    const request: AircraftRequest = this.aircraftForm.value;

    if (this.editingId) {
      this.apiService.updateAircraft(this.editingId, request).subscribe({
        next: () => {
          this.loadAircraft();
          this.resetForm();
        },
        error: (err) => console.error('Error updating aircraft:', err)
      });
    } else {
      this.apiService.createAircraft(request).subscribe({
        next: () => {
          this.loadAircraft();
          this.resetForm();
        },
        error: (err) => console.error('Error creating aircraft:', err)
      });
    }
  }

  editAircraft(a: Aircraft): void {
    this.editingId = a.id;
    this.aircraftForm.patchValue(a);
    this.showForm = true;
  }

  deleteAircraft(id: number): void {
    if (confirm('Are you sure you want to delete this aircraft?')) {
      this.apiService.deleteAircraft(id).subscribe({
        next: () => this.loadAircraft(),
        error: (err) => console.error('Error deleting aircraft:', err)
      });
    }
  }

  resetForm(): void {
    this.aircraftForm.reset();
    this.editingId = null;
    this.showForm = false;
  }
}
