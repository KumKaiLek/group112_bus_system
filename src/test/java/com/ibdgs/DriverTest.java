package com.ibdgs;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Driver class.
 *
 * Covers conditions D1–D5 with at least 3 test cases per condition:
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
            "23@#bc45AB",      // valid driverID
            "John Smith",      // name
            5,                 // experienceYears
            "Light",           // licenseType
            "12|Main St|Melbourne|VIC|Australia",  // valid address
            "15-06-1990"       // valid birthdate
        );
    }

    // =========================================================================
    // D1: Driver ID Validation
    // =========================================================================

    /**
     * D1 - Normal case: a correctly formatted driver ID should be accepted.
     * Format: 2 digits (2-9) | 6 middle chars with ≥2 special | 2 uppercase letters
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
        // Only 8 characters — must throw
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateDriverID("23@#bcAB"));
    }

    /**
     * D1 - Invalid input: first character is a digit outside 2–9 (here: '1').
     */
    @Test
    @DisplayName("D1 - Invalid: first digit is 1 (not in range 2-9)")
    void testD1_FirstDigitOutOfRange() {
        // Starts with '1' which is not allowed
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateDriverID("13@#bc45AB"));
    }

    /**
     * D1 - Invalid input: fewer than 2 special characters in positions 3–8.
     */
    @Test
    @DisplayName("D1 - Invalid: fewer than 2 special characters in middle section")
    void testD1_InsufficientSpecialChars() {
        // Only 1 special char '@' in positions 3-8
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateDriverID("23@abcdeAB"));
    }

    /**
     * D1 - Invalid input: last two characters are not uppercase letters.
     */
    @Test
    @DisplayName("D1 - Invalid: last two characters are not uppercase letters")
    void testD1_LastCharsNotUppercase() {
        // Ends with 'ab' (lowercase) — must throw
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateDriverID("23@#bc45ab"));
    }

    /**
     * D1 - Edge case: exactly 2 special characters in positions 3–8 (minimum requirement).
     */
    @Test
    @DisplayName("D1 - Edge: exactly 2 special characters (minimum) is accepted")
    void testD1_ExactlyTwoSpecialChars() {
        // "56@!abcdGH": 2 specials @,! at positions 3-4 — should pass
        assertDoesNotThrow(() -> Driver.validateDriverID("56@!abcdGH"));
    }

    // =========================================================================
    // D2: Address Format Validation
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
        // Missing Country — only 4 parts
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateAddress("42|Collins St|Melbourne|VIC"));
    }

    /**
     * D2 - Edge case: address with a blank component should be rejected.
     */
    @Test
    @DisplayName("D2 - Edge: address with blank component is rejected")
    void testD2_BlankComponent() {
        // City field is empty
        assertThrows(IllegalArgumentException.class,
            () -> Driver.validateAddress("42|Collins St||VIC|Australia"));
    }

    // =========================================================================
    // D3: Birthdate Format Validation
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
    // D4: License Update Restriction (>10 years experience)
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
        // 10 years — change should be allowed (condition is strictly > 10)
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
        // 11 years — change must be blocked
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
    // D5: Immutable Fields (driverID and name)
    // =========================================================================

    /**
     * D5 - Normal case (immutability check): attempting to set driverID throws exception.
     */
    @Test
    @DisplayName("D5 - Normal: setDriverID always throws UnsupportedOperationException")
    void testD5_DriverIDImmutable() {
        Driver driver = validDriver();
        assertThrows(UnsupportedOperationException.class,
            () -> driver.setDriverID("99@@ef78ZZ"));
    }

    /**
     * D5 - Normal case (immutability check): attempting to set name throws exception.
     */
    @Test
    @DisplayName("D5 - Normal: setName always throws UnsupportedOperationException")
    void testD5_NameImmutable() {
        Driver driver = validDriver();
        assertThrows(UnsupportedOperationException.class,
            () -> driver.setName("New Name"));
    }

    /**
     * D5 - Edge case: confirm that driverID remains unchanged after a failed update attempt.
     */
    @Test
    @DisplayName("D5 - Edge: driverID value is unchanged after failed set attempt")
    void testD5_DriverIDUnchangedAfterException() {
        Driver driver = validDriver();
        String originalID = driver.getDriverID();
        try {
            driver.setDriverID("99@@ef78ZZ");
        } catch (UnsupportedOperationException e) {
            // Expected — verify the ID has not changed
        }
        assertEquals(originalID, driver.getDriverID());
    }
}
