import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FlightStatus, AircraftStatus, BookingStatus } from '@core/models';

type StatusType = FlightStatus | AircraftStatus | BookingStatus | string;

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],  templateUrl: './status-badge.component.html',  styleUrl: './status-badge.component.scss'
})
export class StatusBadgeComponent {
  @Input() status!: StatusType;

  getStatusClass(status: StatusType): string {
    return status.toLowerCase().replace(' ', '_').replace('-', '_');
  }
}
