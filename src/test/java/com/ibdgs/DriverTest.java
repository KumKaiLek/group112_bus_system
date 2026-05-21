package com.ibdgs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Driver class.
 *
 * Covers conditions D1–D5 with exactly 3 test cases per condition (15 total):
 *   D1 - Driver ID validation rules
 *   D2 - Address format validation
 *   D3 - Birthdate format validation
 *   D4 - License update restriction (>10 years experience)
 *   D5 - Immutable fields (driverID and name)
 *
 * Each condition includes: normal case, invalid input, and edge case.
 */
class DriverTest {

    // -------------------------------------------------------------------------
    // Helper: a valid driver used as a base for most tests
    // -------------------------------------------------------------------------

    /**
     * Creates a valid Driver object for reuse in tests.
     * driverID "23@#bc45AB": digits 2,3 | specials @,# at pos 3-8 | uppercase AB at end
     */
    private Driver validDriver() {
        return new Driver(
            "23@#bc45AB",
            "John Smith",
            5,
            "Light",
            "12|Main St|Melbourne|VIC|Australia",
            "15-06-1990"
        );
    }

    // =========================================================================
    // D1: Driver ID Validation (3 tests)
    // =========================================================================

    /**
     * D1 - Normal case: a correctly formatted driver ID should be accepted.
     */
    @Test
    @DisplayName("D1 - Normal: valid driverID is accepted")
    void testD1_ValidDriverID() {
        // "34!!abcdEF": first 2 are digits 3,4; positions 3-8 have !,!; last 2 are E,F
        assertDoesNotThrow(() -> Driver.validateDriverID("34!!abcdEF"));
    }

    /**
     * D1 - Invalid input: driverID shorter than 10 characters should be rejected.
     */
    @Test
    @DisplayName("D1 - Invalid: driverID shorter than 10 characters")
    void testD1_TooShortDriverID() {
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateDriverID("23@#bcAB"));
    }

    /**
     * D1 - Edge case: exactly 2 special characters in positions 3–8 (minimum requirement).
     */
    @Test
    @DisplayName("D1 - Edge: exactly 2 special characters (minimum) is accepted")
    void testD1_ExactlyTwoSpecialChars() {
        assertDoesNotThrow(() -> Driver.validateDriverID("56@!abcdGH"));
    }

    // =========================================================================
    // D2: Address Format Validation (3 tests)
    // =========================================================================

    /**
     * D2 - Normal case: a correctly formatted address should be accepted.
     */
    @Test
    @DisplayName("D2 - Normal: valid address format is accepted")
    void testD2_ValidAddress() {
        assertDoesNotThrow(() ->
            Driver.validateAddress("42|Collins St|Melbourne|VIC|Australia"));
    }

    /**
     * D2 - Invalid input: address missing a component (only 4 parts instead of 5).
     */
    @Test
    @DisplayName("D2 - Invalid: address with only 4 components is rejected")
    void testD2_TooFewComponents() {
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateAddress("42|Collins St|Melbourne|VIC"));
    }

    /**
     * D2 - Edge case: address with a blank component should be rejected.
     */
    @Test
    @DisplayName("D2 - Edge: address with blank component is rejected")
    void testD2_BlankComponent() {
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateAddress("42|Collins St||VIC|Australia"));
    }

    // =========================================================================
    // D3: Birthdate Format Validation (3 tests)
    // =========================================================================

    /**
     * D3 - Normal case: a birthdate in DD-MM-YYYY format should be accepted.
     */
    @Test
    @DisplayName("D3 - Normal: valid birthdate DD-MM-YYYY is accepted")
    void testD3_ValidBirthdate() {
        assertDoesNotThrow(() -> Driver.validateBirthdate("01-01-1990"));
    }

    /**
     * D3 - Invalid input: birthdate in YYYY-MM-DD format should be rejected.
     */
    @Test
    @DisplayName("D3 - Invalid: birthdate in YYYY-MM-DD format is rejected")
    void testD3_WrongFormat() {
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateBirthdate("1990-01-01"));
    }

    /**
     * D3 - Edge case: month value of 13 should be rejected.
     */
    @Test
    @DisplayName("D3 - Edge: invalid month 13 is rejected")
    void testD3_InvalidMonth() {
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateBirthdate("01-13-1990"));
    }

    // =========================================================================
    // D4: License Update Restriction (3 tests)
    // =========================================================================

    /**
     * D4 - Normal case: licence type can be changed for driver with ≤ 10 years experience.
     */
    @Test
    @DisplayName("D4 - Normal: licenseType can be changed with 10 or fewer years experience")
    void testD4_LicenseChangeAllowed() {
        Driver driver = new Driver(
            "23@#bc45AB", "Jane Doe", 10, "Light",
            "5|Bridge Rd|Richmond|VIC|Australia", "20-03-1985"
        );
        assertDoesNotThrow(() -> driver.setLicenseType("Medium"));
    }

    /**
     * D4 - Invalid input: licence type cannot be changed when experienceYears > 10.
     */
    @Test
    @DisplayName("D4 - Invalid: licenseType cannot be changed with more than 10 years experience")
    void testD4_LicenseChangeForbidden() {
        Driver driver = new Driver(
            "23@#bc45AB", "Jane Doe", 11, "Heavy",
            "5|Bridge Rd|Richmond|VIC|Australia", "20-03-1980"
        );
        assertThrows(IllegalStateException.class,
            () -> driver.setLicenseType("Light"));
    }

    /**
     * D4 - Edge case: exactly 11 years is the boundary — change should be blocked.
     */
    @Test
    @DisplayName("D4 - Edge: exactly 11 years experience blocks license change")
    void testD4_ExactlyElevenYears() {
        Driver driver = new Driver(
            "45@!cd67GH", "Sam Lee", 11, "PublicTransport",
            "1|High St|Sydney|NSW|Australia", "10-10-1975"
        );
        assertThrows(IllegalStateException.class,
            () -> driver.setLicenseType("Medium"));
    }

    // =========================================================================
    // D5: Immutable Fields (3 tests)
    // =========================================================================

    /**
     * D5 - Normal case: attempting to set driverID throws UnsupportedOperationException.
     */
    @Test
    @DisplayName("D5 - Normal: setDriverID always throws UnsupportedOperationException")
    void testD5_DriverIDImmutable() {
        Driver driver = validDriver();
        assertThrows(UnsupportedOperationException.class,
            () -> driver.setDriverID("99@@ef78ZZ"));
    }

    /**
     * D5 - Invalid input: attempting to set name throws UnsupportedOperationException.
     */
    @Test
    @DisplayName("D5 - Invalid: setName always throws UnsupportedOperationException")
    void testD5_NameImmutable() {
        Driver driver = validDriver();
        assertThrows(UnsupportedOperationException.class,
            () -> driver.setName("New Name"));
    }

    /**
     * D5 - Edge case: driverID remains unchanged after a failed update attempt.
     */
    @Test
    @DisplayName("D5 - Edge: driverID value is unchanged after failed set attempt")
    void testD5_DriverIDUnchangedAfterException() {
        Driver driver = validDriver();
        String originalID = driver.getDriverID();
        try {
            driver.setDriverID("99@@ef78ZZ");
        } catch (UnsupportedOperationException e) {
            // Expected
        }
        assertEquals(originalID, driver.getDriverID());
    }
}