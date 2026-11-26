# Getting Started with Airport Management Frontend

## Prerequisites

- **Node.js**: v18.0.0 or higher
- **npm**: v9.0.0 or higher
- **Backend API**: Running on http://localhost:8080

Verify installations:
```bash
node --version    # Should be v18.x or higher
npm --version     # Should be v9.x or higher
```

## Installation & Setup

### 1. Navigate to Project Directory

```bash
cd C:\Users\Pett\IdeaProjects\SpringDemos\airport-app
```

### 2. Install Dependencies

```bash
npm install
```

This will install all required packages specified in `package.json`.

### 3. Configure Backend Connection (Optional)

If your backend API is not running on `http://localhost:8080`, update `proxy.conf.json`:

```json
{
  "/api": {
    "target": "http://your-backend-url:port",
    "secure": false,
    "changeOrigin": true
  }
}
```

### 4. Start Development Server

```bash
npm start
```

The application will start on **http://localhost:4200**

Your browser should automatically open the application. If not, manually navigate to `http://localhost:4200`.

## Application Features

### 1. Dashboard
- **Statistics Overview**: Total counts of flights, aircraft, passengers, and bookings
- **Upcoming Flights**: Shows next flights sorted by departure time
- **Aircraft Status**: Breakdown by status (Available, In Use, Maintenance, Retired)
- **Booking Status**: Breakdown by booking status (Confirmed, Checked-in, Cancelled)
- **Recent Bookings**: Latest bookings with passenger and flight details

### 2. Flights Management
- **View All Flights**: Table showing flight details
- **Create Flight**: Add new flights with departure/arrival times
- **Update Flight**: Edit existing flight information
- **Delete Flight**: Remove flights from the system
- **Status Tracking**: Visual status badges for each flight

### 3. Aircraft Management
- **View All Aircraft**: Inventory of all aircraft
- **Add Aircraft**: Register new aircraft with capacity
- **Update Aircraft**: Edit aircraft details
- **Delete Aircraft**: Remove aircraft from inventory
- **Status Monitoring**: Track availability and maintenance status

### 4. Passengers Management
- **View All Passengers**: Complete passenger registry
- **Register Passenger**: Add new passengers with contact info
- **Update Passenger**: Edit passenger details
- **Delete Passenger**: Remove passenger records
- **Email & Passport**: Track passenger identification

### 5. Bookings Management
- **View All Bookings**: All flight bookings
- **Create Booking**: Book a passenger on a flight
- **Assign Seats**: Allocate specific seat numbers
- **Update Booking**: Change booking details
- **Delete Booking**: Cancel bookings
- **Check-in**: Mark passengers as checked in

## Form Usage

All forms follow a consistent pattern:

1. **Click "+ New [Entity]" button** to show the form
2. **Fill in Required Fields** (marked with *)
3. **Submit**: 
   - For new: Creates the entity
   - For existing: Updates the entity
4. **Reset**: Clears all form fields
5. **Cancel**: Hides the form

### Field Validations

- **Text Fields**: Max length constraints
- **Email**: Must be valid email format
- **Numbers**: Must be positive integers
- **Dates**: DateTime format required
- **Dropdowns**: Status selection required

## Common Tasks

### Create a New Flight

1. Go to **Flights** section
2. Click **+ New Flight**
3. Fill in:
   - Flight Number (e.g., "AA100")
   - Origin airport (e.g., "JFK")
   - Destination airport (e.g., "LAX")
   - Departure date/time
   - Arrival date/time
   - Status (choose from dropdown)
   - Aircraft ID (must exist)
4. Click **Create Flight**

### Register a Passenger

1. Go to **Passengers** section
2. Click **+ New Passenger**
3. Fill in:
   - First Name
   - Last Name
   - Email address (valid email format)
   - Passport Number
4. Click **Create Passenger**

### Create a Booking

1. Go to **Bookings** section
2. Click **+ New Booking**
3. Fill in:
   - Passenger ID (ID of registered passenger)
   - Flight ID (ID of existing flight)
   - Seat Number (e.g., "1A", "12B")
   - Status (CONFIRMED, CHECKED_IN, or CANCELLED)
4. Click **Create Booking**

### Edit an Entity

1. Find the entity in the table
2. Click **Edit** button
3. Form will populate with current data
4. Make changes
5. Click **Update [Entity]**

### Delete an Entity

1. Find the entity in the table
2. Click **Delete** button
3. Confirm deletion in the dialog
4. Entity will be removed

## Troubleshooting

### "Cannot GET /" or Blank Page

**Solution**: Check that the development server is running
```bash
npm start
```

### API Connection Errors

**Problem**: "Cannot connect to localhost:8080"

**Solutions**:
1. Verify backend API is running on port 8080
2. Check proxy.conf.json has correct backend URL
3. Restart development server after changing proxy config
4. Check browser console (F12) for specific error messages

### Form Not Submitting

**Problem**: Submit button is disabled

**Solutions**:
1. All required fields (marked with *) must be filled
2. Email must be valid format
3. Numbers must be positive
4. Check browser console for validation errors

### Empty Tables

**Problem**: "No [entity] found"

**Solutions**:
1. Create some entities first
2. Check if backend is returning data
3. Open browser console (F12) to see network requests
4. Verify backend API endpoints are accessible

### Date Format Issues

**Problem**: Dates showing incorrectly

**Solutions**:
1. Use format: YYYY-MM-DD (for dates)
2. Use format: HH:MM (for times)
3. The app will convert to local timezone
4. Browser's locale settings will affect display

## Development Workflow

### File Structure
- `/src/app/core/` - Core services and models
- `/src/app/shared/` - Reusable components
- `/src/app/features/` - Feature pages (dashboard, flights, etc.)

### Adding a New Feature

1. Create new folder in `/src/app/features/`
2. Create component file `feature.component.ts`
3. Add route to `app.routes.ts`
4. Add navigation link to `header.component.ts`
5. Update service if new API endpoints needed

### Styling

The app uses SCSS for styling. To customize:
1. Edit component styles in component file
2. Edit global styles in `src/styles.scss`
3. Use primary color: `#1a5490`

## API Endpoints Reference

### Flights
```
GET    /api/flights              # Get all flights
GET    /api/flights/:id          # Get specific flight
POST   /api/flights              # Create flight
PUT    /api/flights/:id          # Update flight
DELETE /api/flights/:id          # Delete flight
```

### Aircraft
```
GET    /api/aircraft             # Get all aircraft
GET    /api/aircraft/:id         # Get specific aircraft
POST   /api/aircraft             # Create aircraft
PUT    /api/aircraft/:id         # Update aircraft
DELETE /api/aircraft/:id         # Delete aircraft
```

### Passengers
```
GET    /api/passengers           # Get all passengers
GET    /api/passengers/:id       # Get specific passenger
POST   /api/passengers           # Create passenger
PUT    /api/passengers/:id       # Update passenger
DELETE /api/passengers/:id       # Delete passenger
```

### Bookings
```
GET    /api/bookings             # Get all bookings
GET    /api/bookings/:id         # Get specific booking
GET    /api/bookings/flight/:id  # Get bookings for flight
POST   /api/bookings             # Create booking
PUT    /api/bookings/:id         # Update booking
DELETE /api/bookings/:id         # Delete booking
```

## Browser Console Tips

Open browser console with **F12** or **Ctrl+Shift+I** to:
- Check error messages
- Monitor network requests
- Debug issues
- See application logs

## Performance Tips

1. **Clear Browser Cache**: Ctrl+Shift+Delete if changes not showing
2. **Hard Refresh**: Ctrl+F5 to reload all files
3. **Keep Console Closed**: Improves performance slightly
4. **Use Modern Browser**: Chrome, Firefox, or Edge recommended

## Useful Commands

```bash
npm start              # Start development server
npm run build          # Build for production
npm test               # Run unit tests
npm run watch          # Build and watch for changes
```

## Support & Next Steps

1. Check the main **README.md** for more details
2. Review **PROJECT_STRUCTURE.md** for file organization
3. Inspect **Angular** documentation for framework details
4. Check backend API documentation for data formats

## Common Status Values

**Flight Status**: SCHEDULED, DELAYED, BOARDING, DEPARTED, LANDED, CANCELLED

**Aircraft Status**: AVAILABLE, IN_USE, MAINTENANCE, RETIRED

**Booking Status**: CONFIRMED, CHECKED_IN, CANCELLED

## Tips for Success

✅ **Do:**
- Create test data first (aircraft, passengers)
- Then create flights
- Finally create bookings
- Use browser console to debug
- Restart server if changes not showing

❌ **Don't:**
- Forget to fill required fields (*)
- Use invalid email formats
- Create flights with non-existent aircraft IDs
- Try to run without backend API
- Skip checking console for errors

## Need Help?

1. **Check Console**: F12 → Console tab for error messages
2. **Verify Backend**: Make sure backend is running
3. **Review Code**: Component files have detailed comments
4. **Check Validation**: Form will show which fields are invalid
5. **Try Demo Data**: Create simple test data first

Happy flying! ✈️
