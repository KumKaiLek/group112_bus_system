package com.ibdgs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Route class.
 *
 * Covers conditions R1–R5 with exactly 3 test cases per condition (15 total):
 *   R1 - Route ID must be exactly 6 alphanumeric characters
 *   R2 - Distance cannot decrease during update
 *   R3 - Routes longer than 100 km must have at least 3 stops
 *   R4 - Express routes cannot have more than 5 stops
 *   R5 - Inactive routes cannot be assigned a bus
 *
 * Each condition includes: normal case, invalid input, and edge case.
 */

public class RouteTest {
    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
 
    private Route validNormalRoute() {
        return new Route("RT0001", 50.0, 4, "Normal", true);
    }
 
    private Route validExpressRoute() {
        return new Route("RT0002", 80.0, 3, "Express", true);
    }
 
 
    // =========================================================================
    // R1: Route ID Validation (3 tests)
    // =========================================================================
 
    /**
     * R1 - Normal case: a valid 6-character alphanumeric route ID should be accepted.
     */
    @Test
    @DisplayName("R1 - Normal: valid 6-character alphanumeric routeID is accepted")
    void testR1_ValidRouteID() {
        assertDoesNotThrow(() -> Route.validateRouteID("RT0001"));
    }
 
    /**
     * R1 - Invalid input: route ID containing special characters should be rejected.
     */
    @Test
    @DisplayName("R1 - Invalid: routeID with special characters is rejected")
    void testR1_SpecialCharacters() {
        assertThrows(IllegalArgumentException.class,
            () -> Route.validateRouteID("RT-001"));
    }
 
    /**
     * R1 - Edge case: exactly 5 characters (one below required length) should be rejected.
     */
    @Test
    @DisplayName("R1 - Edge: routeID with 5 characters is rejected")
    void testR1_TooShortRouteID() {
        assertThrows(IllegalArgumentException.class,
            () -> Route.validateRouteID("RT001"));
    }
 
    // =========================================================================
    // R2: Distance Cannot Decrease During Update (3 tests)
    // =========================================================================
 
    /**
     * R2 - Normal case: increasing distance should be accepted.
     */
    @Test
    @DisplayName("R2 - Normal: increasing distance is allowed")
    void testR2_DistanceIncrease() {
        Route route = validNormalRoute(); // distance = 50.0
        assertDoesNotThrow(() -> route.setDistance(75.0));
        assertEquals(75.0, route.getDistance());
    }
 
    /**
     * R2 - Invalid input: decreasing distance should throw an exception.
     */
    @Test
    @DisplayName("R2 - Invalid: decreasing distance is rejected")
    void testR2_DistanceDecrease() {
        Route route = validNormalRoute(); // distance = 50.0
        assertThrows(IllegalArgumentException.class,
            () -> route.setDistance(30.0));
    }
 
    /**
     * R2 - Edge case: setting distance to the same value should be accepted.
     */
    @Test
    @DisplayName("R2 - Edge: setting distance to the same value is allowed")
    void testR2_SameDistanceAllowed() {
        Route route = validNormalRoute(); // distance = 50.0
        assertDoesNotThrow(() -> route.setDistance(50.0));
    }
 
    // =========================================================================
    // R3: Routes Longer Than 100 km Must Have At Least 3 Stops (3 tests)
    // =========================================================================
 
    /**
     * R3 - Normal case: a route over 100 km with 4 stops should be accepted.
     */
    @Test
    @DisplayName("R3 - Normal: route over 100 km with 4 stops is accepted")
    void testR3_LongRouteWithEnoughStops() {
        assertDoesNotThrow(() -> Route.checkStopCountForDistance(150.0, 4));
    }
 
    /**
     * R3 - Invalid input: a route over 100 km with only 2 stops should be rejected.
     */
    @Test
    @DisplayName("R3 - Invalid: route over 100 km with 2 stops is rejected")
    void testR3_LongRouteWithTooFewStops() {
        assertThrows(IllegalArgumentException.class,
            () -> Route.checkStopCountForDistance(120.0, 2));
    }
 
    /**
     * R3 - Edge case: a route of exactly 100 km with only 2 stops should be accepted
     * (the rule applies strictly above 100 km).
     */
    @Test
    @DisplayName("R3 - Edge: route of exactly 100 km with 2 stops is accepted (boundary)")
    void testR3_ExactlyOneHundredKm() {
        assertDoesNotThrow(() -> Route.checkStopCountForDistance(100.0, 2));
    }
 
    // =========================================================================
    // R4: Express Routes Cannot Have More Than 5 Stops (3 tests)
    // =========================================================================
 
    /**
     * R4 - Normal case: an express route with 4 stops should be accepted.
     */
    @Test
    @DisplayName("R4 - Normal: express route with 4 stops is accepted")
    void testR4_ExpressWithinStopLimit() {
        assertDoesNotThrow(() -> Route.checkExpressStopLimit("Express", 4));
    }
 
    /**
     * R4 - Invalid input: an express route with 6 stops should be rejected.
     */
    @Test
    @DisplayName("R4 - Invalid: express route with 6 stops is rejected")
    void testR4_ExpressExceedsStopLimit() {
        assertThrows(IllegalArgumentException.class,
            () -> Route.checkExpressStopLimit("Express", 6));
    }
 
    /**
     * R4 - Edge case: an express route with exactly 5 stops should be accepted (boundary).
     */
    @Test
    @DisplayName("R4 - Edge: express route with exactly 5 stops is accepted (boundary)")
    void testR4_ExpressAtStopLimit() {
        assertDoesNotThrow(() -> Route.checkExpressStopLimit("Express", 5));
    }
 
    // =========================================================================
    // R5: Inactive Routes Cannot Be Assigned a Bus (3 tests)
    // =========================================================================
 
    /**
     * R5 - Normal case: assigning a bus to an active route should succeed.
     */
    @Test
    @DisplayName("R5 - Normal: assigning a bus to an active route is allowed")
    void testR5_AssignBusToActiveRoute() {
        Route route = validNormalRoute(); // active = true
        assertDoesNotThrow(() -> route.assignBus("12345678"));
        assertEquals("12345678", route.getAssignedBusID());
    }
 
    /**
     * R5 - Invalid input: assigning a bus to an inactive route should throw an exception.
     */
    @Test
    @DisplayName("R5 - Invalid: assigning a bus to an inactive route is rejected")
    void testR5_AssignBusToInactiveRoute() {
        Route route = new Route("RT0004", 40.0, 2, "Normal", false);
        assertThrows(IllegalStateException.class,
            () -> route.assignBus("12345678"));
    }
 
    /**
     * R5 - Edge case: deactivating a route with an assigned bus should automatically
     * unassign the bus, leaving assignedBusID as null.
     */
    @Test
    @DisplayName("R5 - Edge: deactivating a route unassigns its bus automatically")
    void testR5_DeactivationUnassignsBus() {
        Route route = validNormalRoute(); // active = true
        route.assignBus("12345678");
        assertEquals("12345678", route.getAssignedBusID());
 
        route.setActive(false);
 
        assertNull(route.getAssignedBusID());
    }
}
