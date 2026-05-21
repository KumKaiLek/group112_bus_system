package com.ibdgs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Bus class.
 *
 * Covers conditions B1–B5 with exactly 3 test cases per condition (15 total):
 *   B1 - Bus ID must be exactly 8 digits
 *   B2 - Capacity cannot increase during update
 *   B3 - Drivers older than 50 cannot drive buses with capacity >= 50
 *   B4 - Only drivers with >= 5 years experience can drive electric buses
 *   B5 - Only Heavy or PublicTransport licence holders can drive electric/hybrid buses
 *
 * Each condition includes: normal case, invalid input, and edge case.
 */
class BusTest {

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Bus validDieselBus() {
        return new Bus("12345678", 40, 75.0, "Diesel");
    }

    private Bus validElectricBus() {
        return new Bus("87654321", 30, 90.0, "Electricity");
    }

    private Bus validHybridBus() {
        return new Bus("11223344", 45, 50.0, "Hybrid");
    }

    // =========================================================================
    // B1: Bus ID Validation (3 tests)
    // =========================================================================

    /**
     * B1 - Normal case: a valid 8-digit bus ID should be accepted.
     */
    @Test
    @DisplayName("B1 - Normal: valid 8-digit busID is accepted")
    void testB1_ValidBusID() {
        assertDoesNotThrow(() -> Bus.validateBusID("12345678"));
    }

    /**
     * B1 - Invalid input: bus ID containing non-digit characters should be rejected.
     */
    @Test
    @DisplayName("B1 - Invalid: busID with letters is rejected")
    void testB1_NonDigitCharacters() {
        assertThrows(IllegalArgumentException.class,
            () -> Bus.validateBusID("1234AB78"));
    }

    /**
     * B1 - Edge case: exactly 7 digits (one below required length) should be rejected.
     */
    @Test
    @DisplayName("B1 - Edge: busID with 7 digits is rejected")
    void testB1_TooShortBusID() {
        assertThrows(IllegalArgumentException.class,
            () -> Bus.validateBusID("1234567"));
    }

    // =========================================================================
    // B2: Capacity Cannot Increase During Update (3 tests)
    // =========================================================================

    /**
     * B2 - Normal case: decreasing capacity should be accepted.
     */
    @Test
    @DisplayName("B2 - Normal: decreasing capacity is allowed")
    void testB2_CapacityDecrease() {
        Bus bus = validDieselBus(); // capacity = 40
        assertDoesNotThrow(() -> bus.setCapacity(30));
        assertEquals(30, bus.getCapacity());
    }

    /**
     * B2 - Invalid input: increasing capacity should throw an exception.
     */
    @Test
    @DisplayName("B2 - Invalid: increasing capacity is rejected")
    void testB2_CapacityIncrease() {
        Bus bus = validDieselBus(); // capacity = 40
        assertThrows(IllegalArgumentException.class,
            () -> bus.setCapacity(50));
    }

    /**
     * B2 - Edge case: setting capacity to the same value should be accepted.
     */
    @Test
    @DisplayName("B2 - Edge: setting capacity to the same value is allowed")
    void testB2_SameCapacityAllowed() {
        Bus bus = validDieselBus(); // capacity = 40
        assertDoesNotThrow(() -> bus.setCapacity(40));
    }

    // =========================================================================
    // B3: Drivers Older Than 50 Cannot Drive Buses With Capacity >= 50 (3 tests)
    // =========================================================================

    /**
     * B3 - Normal case: driver aged 45 can drive a bus with capacity 60.
     */
    @Test
    @DisplayName("B3 - Normal: driver aged 45 can drive bus with capacity 60")
    void testB3_YoungDriverLargeBus() {
        Bus bus = new Bus("11111111", 60, 80.0, "Diesel");
        assertDoesNotThrow(() -> bus.checkDriverAgeRestriction(45));
    }

    /**
     * B3 - Invalid input: driver aged 55 cannot drive a bus with capacity 50.
     */
    @Test
    @DisplayName("B3 - Invalid: driver older than 50 cannot drive bus with capacity >= 50")
    void testB3_OldDriverLargeBus() {
        Bus bus = new Bus("22222222", 50, 70.0, "Diesel");
        assertThrows(IllegalArgumentException.class,
            () -> bus.checkDriverAgeRestriction(55));
    }

    /**
     * B3 - Edge case: driver aged exactly 50 with bus capacity exactly 50 — allowed.
     */
    @Test
    @DisplayName("B3 - Edge: driver aged 50 with bus capacity 50 is allowed (boundary)")
    void testB3_BoundaryAgeAndCapacity() {
        Bus bus = new Bus("33333333", 50, 60.0, "Diesel");
        assertDoesNotThrow(() -> bus.checkDriverAgeRestriction(50));
    }

    // =========================================================================
    // B4: Only Drivers with >= 5 Years Experience Can Drive Electric Buses (3 tests)
    // =========================================================================

    /**
     * B4 - Normal case: driver with 10 years experience can drive an electric bus.
     */
    @Test
    @DisplayName("B4 - Normal: driver with 10 years experience can drive electric bus")
    void testB4_SufficientExperience() {
        Bus bus = validElectricBus();
        assertDoesNotThrow(() -> bus.checkElectricExperienceRestriction(10));
    }

    /**
     * B4 - Invalid input: driver with 4 years experience cannot drive an electric bus.
     */
    @Test
    @DisplayName("B4 - Invalid: driver with 4 years experience cannot drive electric bus")
    void testB4_InsufficientExperience() {
        Bus bus = validElectricBus();
        assertThrows(IllegalArgumentException.class,
            () -> bus.checkElectricExperienceRestriction(4));
    }

    /**
     * B4 - Edge case: driver with exactly 5 years experience can drive an electric bus (boundary).
     */
    @Test
    @DisplayName("B4 - Edge: driver with exactly 5 years experience can drive electric bus")
    void testB4_ExactlyFiveYears() {
        Bus bus = validElectricBus();
        assertDoesNotThrow(() -> bus.checkElectricExperienceRestriction(5));
    }

    // =========================================================================
    // B5: Only Heavy/PublicTransport Licence Can Drive Electric or Hybrid Buses (3 tests)
    // =========================================================================

    /**
     * B5 - Normal case: Heavy licence holder can drive an electric bus.
     */
    @Test
    @DisplayName("B5 - Normal: Heavy licence holder can drive electric bus")
    void testB5_HeavyLicenceElectric() {
        Bus bus = validElectricBus();
        assertDoesNotThrow(() -> bus.checkLicenceRestriction("Heavy"));
    }

    /**
     * B5 - Invalid input: Car licence holder cannot drive an electric bus.
     */
    @Test
    @DisplayName("B5 - Invalid: Car licence holder cannot drive electric bus")
    void testB5_CarLicenceElectric() {
        Bus bus = validElectricBus();
        assertThrows(IllegalArgumentException.class,
            () -> bus.checkLicenceRestriction("Car"));
    }

    /**
     * B5 - Edge case: Car licence holder CAN drive a Diesel bus (no restriction applies).
     */
    @Test
    @DisplayName("B5 - Edge: Car licence holder can drive diesel bus (no restriction)")
    void testB5_CarLicenceDiesel() {
        Bus bus = validDieselBus();
        assertDoesNotThrow(() -> bus.checkLicenceRestriction("Car"));
    }
}