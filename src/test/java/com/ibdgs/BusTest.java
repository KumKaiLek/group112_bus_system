package com.ibdgs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Bus class.
 *
 * Covers conditions B1–B5 with at least 3 test cases per condition:
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
    // Helper: create a basic valid Bus
    // -------------------------------------------------------------------------

    /** Creates a valid Diesel bus for reuse in tests. */
    private Bus validDieselBus() {
        return new Bus("12345678", 40, 75.0, "Diesel");
    }

    /** Creates a valid Electric bus for B4/B5 restriction tests. */
    private Bus validElectricBus() {
        return new Bus("87654321", 30, 90.0, "Electricity");
    }

    /** Creates a valid Hybrid bus for B5 restriction tests. */
    private Bus validHybridBus() {
        return new Bus("11223344", 45, 50.0, "Hybrid");
    }

    // =========================================================================
    // B1: Bus ID Validation (exactly 8 digits)
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
     * B1 - Invalid input: bus ID with fewer than 8 digits should be rejected.
     */
    @Test
    @DisplayName("B1 - Invalid: busID shorter than 8 characters is rejected")
    void testB1_TooShortBusID() {
        assertThrows(IllegalArgumentException.class,
            () -> Bus.validateBusID("1234567"));
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
     * B1 - Edge case: exactly 9 digits (one too many) should be rejected.
     */
    @Test
    @DisplayName("B1 - Edge: busID with 9 digits is rejected")
    void testB1_TooLongBusID() {
        assertThrows(IllegalArgumentException.class,
            () -> Bus.validateBusID("123456789"));
    }

    // =========================================================================
    // B2: Capacity Cannot Increase During Update
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
     * B2 - Edge case: setting capacity to the same value should be accepted
     * (it's not an increase).
     */
    @Test
    @DisplayName("B2 - Edge: setting capacity to the same value is allowed")
    void testB2_SameCapacityAllowed() {
        Bus bus = validDieselBus(); // capacity = 40
        assertDoesNotThrow(() -> bus.setCapacity(40));
    }

    // =========================================================================
    // B3: Drivers Older Than 50 Cannot Drive Buses With Capacity >= 50
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
     * B3 - Edge case: driver aged exactly 51 with a bus capacity of exactly 50 is blocked.
     */
    @Test
    @DisplayName("B3 - Edge: driver aged 51 with bus capacity 50 is rejected")
    void testB3_BoundaryAgeAndCapacity() {
        Bus bus = new Bus("33333333", 50, 60.0, "Diesel");
        assertThrows(IllegalArgumentException.class,
            () -> bus.checkDriverAgeRestriction(51));
    }

    /**
     * B3 - Edge case: driver aged 51 with bus capacity of 49 should be allowed.
     */
    @Test
    @DisplayName("B3 - Edge: driver aged 51 with bus capacity 49 is allowed")
    void testB3_OldDriverSmallBus() {
        Bus bus = new Bus("44444444", 49, 60.0, "Diesel");
        assertDoesNotThrow(() -> bus.checkDriverAgeRestriction(51));
    }

    // =========================================================================
    // B4: Only Drivers with >= 5 Years Experience Can Drive Electric Buses
    // =========================================================================

    /**
     * B4 - Normal case: driver with 5 years experience can drive an electric bus.
     */
    @Test
    @DisplayName("B4 - Normal: driver with 5 years experience can drive electric bus")
    void testB4_SufficientExperience() {
        Bus bus = validElectricBus();
        assertDoesNotThrow(() -> bus.checkElectricExperienceRestriction(5));
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
     * B4 - Edge case: driver with 0 years experience cannot drive an electric bus.
     */
    @Test
    @DisplayName("B4 - Edge: driver with 0 years experience cannot drive electric bus")
    void testB4_ZeroExperience() {
        Bus bus = validElectricBus();
        assertThrows(IllegalArgumentException.class,
            () -> bus.checkElectricExperienceRestriction(0));
    }

    /**
     * B4 - Normal case: experience restriction does not apply to Diesel buses.
     */
    @Test
    @DisplayName("B4 - Normal: experience restriction does not apply to diesel buses")
    void testB4_NoRestrictionForDiesel() {
        Bus bus = validDieselBus();
        // Even 0 years experience is fine for a Diesel bus
        assertDoesNotThrow(() -> bus.checkElectricExperienceRestriction(0));
    }

    // =========================================================================
    // B5: Only Heavy/PublicTransport Licence Can Drive Electric or Hybrid Buses
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
     * B5 - Normal case: PublicTransport licence holder can drive a hybrid bus.
     */
    @Test
    @DisplayName("B5 - Normal: PublicTransport licence holder can drive hybrid bus")
    void testB5_PublicTransportLicenceHybrid() {
        Bus bus = validHybridBus();
        assertDoesNotThrow(() -> bus.checkLicenceRestriction("PublicTransport"));
    }

    /**
     * B5 - Invalid input: Light licence holder cannot drive an electric bus.
     */
    @Test
    @DisplayName("B5 - Invalid: Light licence holder cannot drive electric bus")
    void testB5_LightLicenceElectric() {
        Bus bus = validElectricBus();
        assertThrows(IllegalArgumentException.class,
            () -> bus.checkLicenceRestriction("Light"));
    }

    /**
     * B5 - Invalid input: Medium licence holder cannot drive a hybrid bus.
     */
    @Test
    @DisplayName("B5 - Invalid: Medium licence holder cannot drive hybrid bus")
    void testB5_MediumLicenceHybrid() {
        Bus bus = validHybridBus();
        assertThrows(IllegalArgumentException.class,
            () -> bus.checkLicenceRestriction("Medium"));
    }

    /**
     * B5 - Edge case: Light licence holder CAN drive a Diesel bus (no restriction).
     */
    @Test
    @DisplayName("B5 - Edge: Light licence holder can drive diesel bus (no restriction)")
    void testB5_LightLicenceDiesel() {
        Bus bus = validDieselBus();
        assertDoesNotThrow(() -> bus.checkLicenceRestriction("Light"));
    }
}
