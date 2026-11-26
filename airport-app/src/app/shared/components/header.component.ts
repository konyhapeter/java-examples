import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="header">
      <div class="header-content">
        <h1>✈️ Airport Management System</h1>
        <nav class="nav">
          <a routerLink="/dashboard" routerLinkActive="active">Dashboard</a>
          <a routerLink="/flights" routerLinkActive="active">Flights</a>
          <a routerLink="/aircraft" routerLinkActive="active">Aircraft</a>
          <a routerLink="/passengers" routerLinkActive="active">Passengers</a>
          <a routerLink="/bookings" routerLinkActive="active">Bookings</a>
        </nav>
      </div>
    </header>
  `,
  styles: [`
    .header {
      background-color: #1a5490;
      color: white;
      padding: 1rem 2rem;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }

    .header-content {
      max-width: 1400px;
      margin: 0 auto;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    h1 {
      margin: 0;
      font-size: 1.8rem;
      font-weight: 600;
    }

    .nav {
      display: flex;
      gap: 2rem;
    }

    a {
      color: white;
      text-decoration: none;
      font-weight: 500;
      transition: color 0.3s;
      padding: 0.5rem 1rem;
      border-radius: 4px;

      &:hover {
        background-color: rgba(255,255,255,0.1);
      }

      &.active {
        background-color: rgba(255,255,255,0.2);
        border-bottom: 2px solid #fff;
      }
    }

    @media (max-width: 768px) {
      .header-content {
        flex-direction: column;
        gap: 1rem;
      }

      .nav {
        flex-direction: column;
        gap: 0.5rem;
        width: 100%;
      }
    }
  `]
})
export class HeaderComponent {}
