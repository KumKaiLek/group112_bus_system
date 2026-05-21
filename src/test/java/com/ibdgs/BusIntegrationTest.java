package com.ibdgs;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for BusRepository.
 *
 * Verifies that bus records are correctly stored, rejected, updated,
 * and counted using real TXT file persistence (4 test cases total).
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusIntegrationTest {

    private static final String TEST_FILE = "data/test_buses.txt";
    private BusRepository repo;

    /**
     * Set up a fresh repository and empty TXT file before each test.
     */
    @BeforeEach
    void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_FILE));
        repo = new BusRepository(TEST_FILE);
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

    private Bus makeBus(String id) {
        return new Bus(id, 40, 75.0, "Diesel");
    }

    // =========================================================================
    // IT-B1: Valid buses are stored correctly
    // =========================================================================

    /**
     * IT-B1: Adding a valid bus should persist it to the TXT file
     * and it should be retrievable with all correct field values.
     */
    @Test
    @Order(1)
    @DisplayName("IT-B1: Valid bus is stored and retrievable from TXT file")
    void testIT_B1_ValidBusStoredCorrectly() throws IOException {
        Bus bus = new Bus("12345678", 45, 80.0, "Diesel");
        repo.add(bus);

        Bus retrieved = repo.retrieve("12345678");
        assertNotNull(retrieved, "Bus should be found in the file");
        assertEquals("12345678", retrieved.getBusID());
        assertEquals(45, retrieved.getCapacity());
        assertEquals(80.0, retrieved.getFuelLevel(), 0.001);
        assertEquals("Diesel", retrieved.getFuelType());
    }

    // =========================================================================
    // IT-B2: Invalid buses are rejected and not stored
    // =========================================================================

    /**
     * IT-B2: Adding a bus with a duplicate ID should throw and
     * the file should still contain only the original record.
     */
    @Test
    @Order(2)
    @DisplayName("IT-B2: Duplicate busID is rejected and file is unchanged")
    void testIT_B2_DuplicateBusRejected() throws IOException {
        repo.add(makeBus("12345678"));

        assertThrows(IllegalArgumentException.class,
            () -> repo.add(makeBus("12345678")));

        assertEquals(1, repo.count(), "File should still have only 1 bus record");
    }

    // =========================================================================
    // IT-B3: Updates are persisted correctly to the TXT file
    // =========================================================================

    /**
     * IT-B3: Updating a bus's details should persist the new values
     * and the old values should no longer appear in the file.
     */
    @Test
    @Order(3)
    @DisplayName("IT-B3: Bus update is persisted correctly to TXT file")
    void testIT_B3_UpdatePersistedToFile() throws IOException {
        repo.add(new Bus("55667788", 40, 60.0, "Diesel"));

        Bus updated = new Bus("55667788", 35, 45.0, "Hybrid");
        repo.update(updated);

        Bus retrieved = repo.retrieve("55667788");
        assertNotNull(retrieved);
        assertEquals(35, retrieved.getCapacity(),
            "Capacity should reflect the decrease");
        assertEquals(45.0, retrieved.getFuelLevel(), 0.001,
            "Fuel level should be updated");
        assertEquals("Hybrid", retrieved.getFuelType(),
            "Fuel type should be updated");
    }

    // =========================================================================
    // IT-B4: Record counts are updated correctly
    // =========================================================================

    /**
     * IT-B4: Count should increase with each valid bus added and remain
     * unchanged after a failed duplicate add.
     */
    @Test
    @Order(4)
    @DisplayName("IT-B4: Count updates correctly and stays unchanged after rejected add")
    void testIT_B4_CountUpdatesCorrectly() throws IOException {
        assertEquals(0, repo.count(), "Initial count should be 0");

        repo.add(makeBus("11111111"));
        assertEquals(1, repo.count(), "Count should be 1 after first add");

        repo.add(makeBus("22222222"));
        assertEquals(2, repo.count(), "Count should be 2 after second add");

        // Duplicate — should not change count
        assertThrows(IllegalArgumentException.class,
            () -> repo.add(makeBus("11111111")));
        assertEquals(2, repo.count(), "Count should remain 2 after rejected duplicate");
    }
}