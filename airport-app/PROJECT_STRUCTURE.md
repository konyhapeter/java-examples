# Project Structure Summary

## Complete Directory Layout

```
airport-app/
├── src/
│   ├── app/
│   │   ├── core/
│   │   │   ├── models/
│   │   │   │   └── index.ts                 # All models, enums, and interfaces
│   │   │   └── services/
│   │   │       └── api.service.ts           # HTTP client for backend API
│   │   │
│   │   ├── shared/
│   │   │   ├── components/
│   │   │   │   ├── header.component.ts      # Navigation header
│   │   │   │   ├── loading.component.ts     # Loading spinner
│   │   │   │   └── status-badge.component.ts # Status indicator badges
│   │   │   └── pipes/
│   │   │       └── date-format.pipe.ts      # Date formatting utility
│   │   │
│   │   ├── features/
│   │   │   ├── dashboard/
│   │   │   │   └── dashboard.component.ts   # Main dashboard overview
│   │   │   │
│   │   │   ├── flights/
│   │   │   │   └── flights.component.ts     # Flights CRUD interface
│   │   │   │
│   │   │   ├── aircraft/
│   │   │   │   └── aircraft.component.ts    # Aircraft CRUD interface
│   │   │   │
│   │   │   ├── passengers/
│   │   │   │   └── passengers.component.ts  # Passengers CRUD interface
│   │   │   │
│   │   │   └── bookings/
│   │   │       └── bookings.component.ts    # Bookings CRUD interface
│   │   │
│   │   ├── app.component.ts                 # Root component
│   │   ├── app.config.ts                    # Application configuration
│   │   └── app.routes.ts                    # Route definitions
│   │
│   ├── main.ts                              # Application entry point
│   ├── index.html                           # HTML template
│   ├── styles.scss                          # Global styles
│   └── favicon.ico
│
├── angular.json                             # Angular CLI configuration
├── tsconfig.json                            # TypeScript configuration
├── tsconfig.app.json                        # App-specific TypeScript config
├── tsconfig.spec.json                       # Testing TypeScript config
├── karma.conf.js                            # Test runner configuration
├── proxy.conf.json                          # Development proxy config
├── package.json                             # Dependencies and scripts
├── .gitignore                               # Git ignore file
└── README.md                                # Project documentation
```

## Key Files Overview

### Core Files

**api.service.ts**
- Provides HTTP methods for all API endpoints
- Handles Aircraft, Passenger, Flight, and Booking resources
- Full CRUD operations for each resource

**Models (index.ts)**
- Enums: FlightStatus, AircraftStatus, BookingStatus
- Interfaces: Aircraft, Passenger, Flight, Booking
- Request interfaces for data submission

### Shared Components

**header.component.ts**
- Navigation bar with links to all sections
- Responsive design with mobile support
- Styled with airport theme colors

**loading.component.ts**
- Reusable loading indicator
- Customizable message
- CSS spinner animation

**status-badge.component.ts**
- Color-coded status indicators
- Supports all status enums
- Used throughout the application

**date-format.pipe.ts**
- Formats dates to readable format (MM/DD/YYYY HH:MM)
- Used in flight and dashboard components

### Feature Components

**dashboard.component.ts**
- Shows statistics cards (flights, aircraft, passengers, bookings)
- Displays upcoming flights sorted by departure time
- Shows aircraft and booking status breakdowns
- Recent bookings list

**flights.component.ts**
- Create, read, update, delete flights
- Form with validation
- Display flights in table with details
- Status badge showing flight state

**aircraft.component.ts**
- Manage aircraft inventory
- Track aircraft status and capacity
- Full CRUD operations
- Registration and model information

**passengers.component.ts**
- Register and manage passengers
- Email validation
- Passport tracking
- Full CRUD operations

**bookings.component.ts**
- Create and manage flight bookings
- Link passengers to flights
- Seat assignment
- Status management (Confirmed, Checked-in, Cancelled)

## Styling System

- **Primary Color**: #1a5490 (Airport Blue)
- **Layout**: Responsive CSS Grid
- **Tables**: Clean, sortable appearance
- **Forms**: Validated with inline feedback
- **Status Colors**: Distinct colors for each status state
- **Mobile**: Fully responsive design

## Configuration Files

**package.json**
- Angular 18+ dependencies
- Dev dependencies for build and testing
- Scripts for development, building, and testing

**tsconfig.json**
- Path aliases: @core/*, @shared/*, @features/*
- Strict type checking enabled
- ES2022 target

**angular.json**
- Build configurations (development and production)
- Proxy configuration pointing to backend
- Asset and style configurations

**proxy.conf.json**
- Development API proxy
- Points to http://localhost:8080/api
- Enables CORS handling in development

## Component Features

### Common Patterns

All CRUD components follow the same pattern:
1. Load data on component init
2. Display in table format
3. Toggle form visibility
4. Form with validation
5. Submit/Update/Delete operations
6. Error handling with console logging

### Form Implementation

- Reactive Forms with FormBuilder
- Real-time validation
- Disabled submit button when invalid
- Reset functionality
- Edit mode for updating records

### Data Flow

1. Component loads data on init using ApiService
2. ApiService makes HTTP requests to backend
3. Data displayed in tables with status badges
4. User interactions trigger create/update/delete operations
5. Success triggers data reload
6. Errors logged to console

## Running the Application

```bash
# Install dependencies
npm install

# Start development server (runs on http://localhost:4200)
npm start

# Build for production
npm run build

# Run tests
npm test
```

## Backend Requirements

The backend must run on http://localhost:8080 and provide these endpoints:
- `/api/flights` - Full CRUD
- `/api/aircraft` - Full CRUD
- `/api/passengers` - Full CRUD
- `/api/bookings` - Full CRUD + GET by flight

All endpoints must accept JSON and return JSON responses matching the model interfaces.
