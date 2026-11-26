# Airport Management System - Frontend

A modern Angular standalone application for managing airport operations including flights, aircraft, passengers, and bookings.

## Features

- **Dashboard**: Overview of key metrics and recent activities
- **Flights Management**: Create, read, update, and delete flights with real-time status tracking
- **Aircraft Management**: Manage aircraft inventory and maintenance status
- **Passengers Management**: Register and manage passenger information
- **Bookings Management**: Handle flight bookings and check-in status

## Technology Stack

- **Framework**: Angular 18+
- **Language**: TypeScript
- **Styling**: SCSS
- **State Management**: RxJS
- **HTTP Client**: Angular HttpClient
- **Build Tool**: Angular CLI

## Project Structure

```
src/app/
├── core/
│   ├── models/          # TypeScript interfaces and enums
│   └── services/        # API service for backend communication
├── shared/
│   ├── components/      # Reusable components (Header, Loading, Status Badge)
│   └── pipes/          # Custom pipes (Date formatting)
├── features/
│   ├── dashboard/      # Dashboard overview
│   ├── flights/        # Flights management
│   ├── aircraft/       # Aircraft management
│   ├── passengers/     # Passengers management
│   └── bookings/       # Bookings management
├── app.routes.ts       # Application routing
├── app.config.ts       # Application configuration
└── app.component.ts    # Root component
```

## Getting Started

### Prerequisites

- Node.js (v18 or higher)
- npm (v9 or higher)

### Installation

```bash
cd airport-app
npm install
```

### Development Server

```bash
npm start
```

Navigate to `http://localhost:4200/`. The application will automatically reload if you change any source files.

### Backend Configuration

The application expects the backend API to be running on `http://localhost:8080`. Update the proxy configuration in `proxy.conf.json` if your backend runs on a different port.

```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

### Build

```bash
npm run build
```

The build artifacts will be stored in the `dist/` directory.

## API Endpoints Used

### Flights
- `GET /api/flights` - Get all flights
- `GET /api/flights/:id` - Get flight by ID
- `POST /api/flights` - Create new flight
- `PUT /api/flights/:id` - Update flight
- `DELETE /api/flights/:id` - Delete flight

### Aircraft
- `GET /api/aircraft` - Get all aircraft
- `GET /api/aircraft/:id` - Get aircraft by ID
- `POST /api/aircraft` - Create new aircraft
- `PUT /api/aircraft/:id` - Update aircraft
- `DELETE /api/aircraft/:id` - Delete aircraft

### Passengers
- `GET /api/passengers` - Get all passengers
- `GET /api/passengers/:id` - Get passenger by ID
- `POST /api/passengers` - Create new passenger
- `PUT /api/passengers/:id` - Update passenger
- `DELETE /api/passengers/:id` - Delete passenger

### Bookings
- `GET /api/bookings` - Get all bookings
- `GET /api/bookings/:id` - Get booking by ID
- `GET /api/bookings/flight/:flightId` - Get bookings for a flight
- `POST /api/bookings` - Create new booking
- `PUT /api/bookings/:id` - Update booking
- `DELETE /api/bookings/:id` - Delete booking

## Styling

The application uses SCSS for styling with a consistent design system:
- Primary color: #1a5490 (Airport Blue)
- Status badge colors for different states
- Responsive grid layouts
- Mobile-friendly design

## Components

### Shared Components

- **HeaderComponent**: Navigation bar with links to all features
- **LoadingComponent**: Loading spinner with customizable message
- **StatusBadgeComponent**: Visual status indicators with color coding

### Pipes

- **DateFormatPipe**: Formats dates as MM/DD/YYYY HH:MM format

## Form Validation

All forms include client-side validation using Angular Reactive Forms:
- Required field validation
- Email validation for passenger emails
- Length constraints matching backend requirements
- Number constraints (positive values only)

## Error Handling

The application includes basic error handling with console logging. For production, consider implementing:
- Global error interceptor
- User-friendly error notifications
- Error logging service

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## License

This project is part of the Spring Demos collection.

## Contributing

When adding new features:
1. Create new components in the appropriate feature module
2. Add models to the core models
3. Extend the API service with new endpoints
4. Follow the existing styling patterns
