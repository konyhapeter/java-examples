package org.example.airportmanagement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AirportManagementApiTest {
    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void configurePostgres(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", postgres::getJdbcUrl);
        properties.add("spring.datasource.username", postgres::getUsername);
        properties.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void clearDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE bookings, flights, passengers, aircraft RESTART IDENTITY CASCADE");
    }

    @Test
    void managesAircraftPassengersFlightsAndCapacityLimitedBookings() throws Exception {
        MvcResult aircraftResult = mockMvc.perform(post("/api/aircraft")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"registration":"N123AB","model":"A320","capacity":1,"status":"AVAILABLE"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registration").value("N123AB"))
                .andReturn();
        long aircraftId = idFrom(aircraftResult);

        long firstPassengerId = createPassenger("Ada", "Lovelace", "ada@example.com", "P1001");
        long secondPassengerId = createPassenger("Grace", "Hopper", "grace@example.com", "P1002");

        MvcResult flightResult = mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(flightJson("AM101", aircraftId, "2026-12-15T09:00:00", "2026-12-15T11:00:00")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.aircraftRegistration").value("N123AB"))
                .andReturn();
        long flightId = idFrom(flightResult);

        mockMvc.perform(get("/api/timetable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].flightNumber").value("AM101"));

        mockMvc.perform(post("/api/flights")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(flightJson("AM102", aircraftId, "2026-12-15T10:30:00", "2026-12-15T12:00:00")))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(firstPassengerId, flightId, "1A")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bookingJson(secondPassengerId, flightId, "1B")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Flight has reached aircraft passenger capacity"));
    }

    private long createPassenger(String firstName, String lastName, String email, String passport) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/passengers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"%s","lastName":"%s","email":"%s","passportNumber":"%s"}
                                """.formatted(firstName, lastName, email, passport)))
                .andExpect(status().isCreated())
                .andReturn();
        return idFrom(result);
    }

    private long idFrom(MvcResult result) throws Exception {
        Number id = com.jayway.jsonpath.JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }

    private String flightJson(String number, long aircraftId, String departure, String arrival) {
        return """
                {"flightNumber":"%s","origin":"JFK","destination":"LHR","departureTime":"%s",
                 "arrivalTime":"%s","status":"SCHEDULED","aircraftId":%d}
                """.formatted(number, departure, arrival, aircraftId);
    }

    private String bookingJson(long passengerId, long flightId, String seatNumber) {
        return """
                {"passengerId":%d,"flightId":%d,"seatNumber":"%s","status":"CONFIRMED"}
                """.formatted(passengerId, flightId, seatNumber);
    }
}
