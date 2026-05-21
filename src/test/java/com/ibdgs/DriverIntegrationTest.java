package com.ibdgs;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for DriverRepository.
 *
 * Verifies that driver records are correctly stored, rejected, updated,
 * and counted using real TXT file persistence (4 test cases total).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DriverIntegrationTest {

    private static final String TEST_FILE = "data/test_drivers.txt";
    private DriverRepository repo;

    /**
     * Set up a fresh repository and empty TXT file before each test.
     */
    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
        repo = new DriverRepository(TEST_FILE);
    }

    /**
     * Clean up the TXT file after each test.
     */
    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private Driver makeDriver(String id) {
        return new Driver(
            id, "Test Driver", 5, "Light",
            "10|Swanston St|Melbourne|VIC|Australia", "01-01-1990"
        );
    }

    // =========================================================================
    // IT-D1: Valid drivers are stored correctly
    // =========================================================================

    /**
     * IT-D1: Adding a valid driver should persist it to the TXT file
     * and it should be retrievable with all correct field values.
     */
    @Test
    @Order(1)
    @DisplayName("IT-D1: Valid driver is stored and retrievable from TXT file")
    void testIT_D1_ValidDriverStoredCorrectly() throws IOException {
        Driver driver = makeDriver("23@#bc45AB");
        repo.add(driver);

        Driver retrieved = repo.retrieve("23@#bc45AB");
        assertNotNull(retrieved, "Driver should be found in the file");
        assertEquals("23@#bc45AB", retrieved.getDriverID());
        assertEquals("Test Driver", retrieved.getName());
        assertEquals(5, retrieved.getExperienceYears());
        assertEquals("01-01-1990", retrieved.getBirthdate());
    }

    // =========================================================================
    // IT-D2: Invalid drivers are rejected and not stored
    // =========================================================================

    /**
     * IT-D2: Adding a driver with a duplicate ID should throw and
     * the file should still contain only one record.
     */
    @Test
    @Order(2)
    @DisplayName("IT-D2: Duplicate driverID is rejected and file is unchanged")
    void testIT_D2_DuplicateDriverRejected() throws IOException {
        repo.add(makeDriver("23@#bc45AB"));

        assertThrows(IllegalArgumentException.class,
            () -> repo.add(makeDriver("23@#bc45AB")));

        assertEquals(1, repo.count(), "File should still have only 1 driver record");
    }

    // =========================================================================
    // IT-D3: Updates are persisted correctly to the TXT file
    // =========================================================================

    /**
     * IT-D3: Updating a driver's details should persist the new values
     * and the old values should no longer appear in the file.
     */
    @Test
    @Order(3)
    @DisplayName("IT-D3: Driver update is persisted correctly to TXT file")
    void testIT_D3_UpdatePersistedToFile() throws IOException {
        repo.add(makeDriver("45@!cd67GH"));

        Driver updated = new Driver(
            "45@!cd67GH", "Test Driver", 5, "Medium",
            "99|New St|Sydney|NSW|Australia", "01-01-1990"
        );
        repo.update(updated);

        Driver retrieved = repo.retrieve("45@!cd67GH");
        assertNotNull(retrieved);
        assertEquals("Medium", retrieved.getLicenseType(),
            "License type should reflect the update");
        assertEquals("99|New St|Sydney|NSW|Australia", retrieved.getAddress(),
            "Address should reflect the update");
    }

    // =========================================================================
    // IT-D4: Record counts are updated correctly
    // =========================================================================

    /**
     * IT-D4: Count should increase with each valid driver added and remain
     * unchanged after a failed duplicate add.
     */
    @Test
    @Order(4)
    @DisplayName("IT-D4: Count updates correctly and stays unchanged after rejected add")
    void testIT_D4_CountUpdatesCorrectly() throws IOException {
        assertEquals(0, repo.count(), "Initial count should be 0");

        repo.add(makeDriver("23@#bc45AB"));
        assertEquals(1, repo.count(), "Count should be 1 after first add");

        repo.add(makeDriver("56@!ef78CD"));
        assertEquals(2, repo.count(), "Count should be 2 after second add");

        // Duplicate — should not change count
        assertThrows(IllegalArgumentException.class,
            () -> repo.add(makeDriver("23@#bc45AB")));
        assertEquals(2, repo.count(), "Count should remain 2 after rejected duplicate");
    }
}