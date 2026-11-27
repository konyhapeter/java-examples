import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Subject, of, throwError } from 'rxjs';
import { Aircraft, AircraftStatus, Flight, FlightStatus } from '@core/models';
import { ApiService } from '@core/services/api.service';
import { FlightsComponent } from './flights.component';

describe('FlightsComponent aircraft selection', () => {
  let fixture: ComponentFixture<FlightsComponent>;
  let component: FlightsComponent;
  let api: jasmine.SpyObj<ApiService>;
  const aircraft: Aircraft[] = [
    { id: 12, model: 'A320', registration: 'HA-ABC', capacity: 180, status: AircraftStatus.AVAILABLE },
    { id: 34, model: 'B737', registration: 'HA-DEF', capacity: 150, status: AircraftStatus.IN_USE }
  ];
  const flight: Flight = {
    id: 1,
    flightNumber: 'AB123',
    origin: 'BUD',
    destination: 'LHR',
    departureTime: new Date('2026-11-27T10:00:00'),
    arrivalTime: new Date('2026-11-27T12:00:00'),
    status: FlightStatus.SCHEDULED,
    aircraftId: 34,
    aircraftRegistration: 'HA-DEF'
  };

  beforeEach(async () => {
    api = jasmine.createSpyObj<ApiService>('ApiService', ['getFlights', 'getAircraft', 'createFlight', 'updateFlight']);
    api.getFlights.and.returnValue(of([]));
    api.getAircraft.and.returnValue(of(aircraft));
    api.createFlight.and.returnValue(of(flight));
    api.updateFlight.and.returnValue(of(flight));

    await TestBed.configureTestingModule({
      imports: [FlightsComponent],
      providers: [{ provide: ApiService, useValue: api }]
    }).compileComponents();
    fixture = TestBed.createComponent(FlightsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('shows model and registration and submits the selected numeric aircraft ID', () => {
    component.toggleForm();
    component.flightForm.patchValue({
      flightNumber: 'AB123',
      origin: 'BUD',
      destination: 'LHR',
      departureTime: '2026-11-27T10:00',
      arrivalTime: '2026-11-27T12:00',
      status: FlightStatus.SCHEDULED
    });
    fixture.detectChanges();

    const select: HTMLSelectElement = fixture.nativeElement.querySelector('#aircraft');
    expect(select.options[1].textContent?.trim()).toBe('A320 - HA-ABC');
    expect(select.options[2].textContent?.trim()).toBe('B737 - HA-DEF');
    expect(component.flightForm.valid).toBeFalse();
    select.value = select.options[1].value;
    select.dispatchEvent(new Event('change'));
    fixture.detectChanges();

    component.submitForm();
    expect(api.createFlight).toHaveBeenCalledWith(jasmine.objectContaining({ aircraftId: 12 }));
  });

  it('preserves the selected aircraft when editing before aircraft finish loading', () => {
    const response = new Subject<Aircraft[]>();
    api.getAircraft.and.returnValue(response);
    component.aircraft = [];
    component.loadAircraft();
    component.editFlight(flight);
    fixture.detectChanges();
    expect(component.aircraftSelectionAvailable).toBeFalse();
    component.submitForm();
    expect(api.updateFlight).not.toHaveBeenCalled();

    response.next(aircraft);
    fixture.detectChanges();
    const select: HTMLSelectElement = fixture.nativeElement.querySelector('#aircraft');
    expect(select.selectedOptions[0].textContent?.trim()).toBe('B737 - HA-DEF');
    component.submitForm();
    expect(api.updateFlight).toHaveBeenCalledWith(1, jasmine.objectContaining({ aircraftId: 34 }));
  });

  it('blocks submission and permits retry when aircraft cannot be loaded', () => {
    spyOn(console, 'error');
    api.getAircraft.and.returnValue(throwError(() => new Error('Failed')));
    component.loadAircraft();
    component.editFlight(flight);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('[role="alert"]').textContent).toContain('Could not load aircraft');
    component.submitForm();
    expect(api.updateFlight).not.toHaveBeenCalled();

    api.getAircraft.and.returnValue(of(aircraft));
    (fixture.nativeElement.querySelector('[role="alert"] button') as HTMLButtonElement).click();
    fixture.detectChanges();
    expect(component.aircraftLoadError).toBeFalse();
    component.submitForm();
    expect(api.updateFlight).toHaveBeenCalled();
  });

  it('requires a new selection when the existing aircraft is missing', () => {
    component.editFlight({ ...flight, aircraftId: 99 });
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('[role="alert"]').textContent).toContain('no longer available');
    component.submitForm();
    expect(api.updateFlight).not.toHaveBeenCalled();
  });
});
