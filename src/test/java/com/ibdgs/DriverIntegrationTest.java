package com.ibdgs;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DriverIntegrationTest {

    // Temporary TXT file used for all integration tests
    private static final String TEST_FILE = "data/test_drivers.txt";

    private DriverRepository repo;

    /**
     * Set up a fresh repository and empty TXT file before each test.
     * Ensures each test starts from a clean state.
     */
    @BeforeEach
    void setUp() throws IOException {
        // Delete the file if it exists from a previous test
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
    // Helper: valid driver objects
    // -------------------------------------------------------------------------

    /** Returns a valid driver for reuse across tests. */
    private Driver makeDriver(String id) {
        return new Driver(
            id,
            "Test Driver",
            5,
            "Light",
            "10|Swanston St|Melbourne|VIC|Australia",
            "01-01-1990"
        );
    }

    // =========================================================================
    // IT-D1: Valid drivers are stored correctly
    // =========================================================================

    /**
     * IT-D1: Adding a valid driver should persist it to the TXT file,
     * and it should be retrievable with correct field values.
     */
    @Test
    @Order(1)
    @DisplayName("IT-D1: Valid driver is stored and retrievable from TXT file")
    void testIT_D1_ValidDriverStoredCorrectly() throws IOException {
        Driver driver = makeDriver("23@#bc45AB");
        repo.add(driver);

        // Retrieve from file and verify all fields
        Driver retrieved = repo.retrieve("23@#bc45AB");
        assertNotNull(retrieved, "Driver should be found in the file");
        assertEquals("23@#bc45AB", retrieved.getDriverID());
        assertEquals("Test Driver",  retrieved.getName());
        assertEquals(5, retrieved.getExperienceYears());
        assertEquals("Light", retrieved.getLicenseType());
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
        Driver driver1 = makeDriver("23@#bc45AB");
        repo.add(driver1);

        // Attempt to add a second driver with the same ID
        Driver driver2 = makeDriver("23@#bc45AB");
        assertThrows(IllegalArgumentException.class, () -> repo.add(driver2));

        // File should still contain exactly 1 record
        assertEquals(1, repo.count(), "File should still have only 1 driver record");
    }

    /**
     * IT-D2: A driver constructed with an invalid driverID should throw
     * at construction time, before it even reaches the repository.
     */
    @Test
    @Order(3)
    @DisplayName("IT-D2: Driver with invalid ID is rejected at construction, not stored")
    void testIT_D2_InvalidDriverNotStored() throws IOException {
        // Constructing a driver with an invalid ID should throw
        assertThrows(IllegalArgumentException.class,
            () -> new Driver("INVALID_ID", "Bad Driver", 3, "Light",
                             "1|Test St|Melbourne|VIC|Australia", "01-01-2000"));

        // Repository should remain empty
        assertEquals(0, repo.count());
    }

    // =========================================================================
    // IT-D3: Updates are persisted correctly to the TXT file
    // =========================================================================

    /**
     * IT-D3: Updating a driver's address should persist the new value
     * and the old value should no longer appear in the file.
     */
    @Test
    @Order(4)
    @DisplayName("IT-D3: Driver update is persisted correctly to TXT file")
    void testIT_D3_UpdatePersistedToFile() throws IOException {
        // Add the original driver
        Driver driver = makeDriver("45@!cd67GH");
        repo.add(driver);

        // Create an updated version with a new address
        Driver updated = new Driver(
            "45@!cd67GH",
            "Test Driver",
            5,
            "Medium",  // updated licenseType
            "99|New St|Sydney|NSW|Australia",  // updated address
            "01-01-1990"
        );
        repo.update(updated);

        // Retrieve and verify the update was persisted
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
     * IT-D4: Count should increase with each valid driver added.
     */
    @Test
    @Order(5)
    @DisplayName("IT-D4: Count increases correctly as drivers are added")
    void testIT_D4_CountUpdatesCorrectly() throws IOException {
        assertEquals(0, repo.count(), "Initial count should be 0");

        repo.add(makeDriver("23@#bc45AB"));
        assertEquals(1, repo.count(), "Count should be 1 after first add");

        repo.add(makeDriver("56@!ef78CD"));
        assertEquals(2, repo.count(), "Count should be 2 after second add");

        repo.add(makeDriver("78##gh12EF"));
        assertEquals(3, repo.count(), "Count should be 3 after third add");
    }

    /**
     * IT-D4: Count should remain unchanged after a failed add (duplicate ID).
     */
    @Test
    @Order(6)
    @DisplayName("IT-D4: Count does not change after rejected duplicate add")
    void testIT_D4_CountUnchangedAfterFailedAdd() throws IOException {
        repo.add(makeDriver("23@#bc45AB"));
        int countBefore = repo.count();

        // Try to add a duplicate — should throw
        assertThrows(IllegalArgumentException.class,
            () -> repo.add(makeDriver("23@#bc45AB")));

        assertEquals(countBefore, repo.count(),
            "Count should be unchanged after rejected duplicate");
    }
}
