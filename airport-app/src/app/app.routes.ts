import { Routes } from '@angular/router';
import { DashboardComponent } from '@features/dashboard/dashboard.component';
import { FlightsComponent } from '@features/flights/flights.component';
import { AircraftComponent } from '@features/aircraft/aircraft.component';
import { PassengersComponent } from '@features/passengers/passengers.component';
import { BookingsComponent } from '@features/bookings/bookings.component';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'flights', component: FlightsComponent },
  { path: 'aircraft', component: AircraftComponent },
  { path: 'passengers', component: PassengersComponent },
  { path: 'bookings', component: BookingsComponent },
  { path: '**', redirectTo: 'dashboard' }
];
