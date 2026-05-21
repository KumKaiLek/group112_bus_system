package com.ibdgs;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BusIntegrationTest {

    // Temporary TXT file used for all integration tests
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

    /** Returns a valid Diesel bus for reuse across tests. */
    private Bus makebus(String id) {
        return new Bus(id, 40, 75.0, "Diesel");
    }

    // =========================================================================
    // IT-B1: Valid buses are stored correctly
    // =========================================================================

    /**
     * IT-B1: Adding a valid bus should persist it to the TXT file,
     * and it should be retrievable with all correct field values.
     */
    @Test
    @Order(1)
    @DisplayName("IT-B1: Valid bus is stored and retrievable from TXT file")
    void testIT_B1_ValidBusStoredCorrectly() throws IOException {
        Bus bus = new Bus("12345678", 45, 80.0, "Diesel");
        repo.add(bus);

        // Retrieve and verify all fields
        Bus retrieved = repo.retrieve("12345678");
        assertNotNull(retrieved, "Bus should be found in the file");
        assertEquals("12345678", retrieved.getBusID());
        assertEquals(45,   retrieved.getCapacity());
        assertEquals(80.0, retrieved.getFuelLevel(), 0.001);
        assertEquals("Diesel", retrieved.getFuelType());
    }

    // =========================================================================
    // IT-B2: Invalid buses are rejected and not stored
    // =========================================================================

    /**
     * IT-B2: Adding a bus with a duplicate ID should throw and the file
     * should still contain only the original record.
     */
    @Test
    @Order(2)
    @DisplayName("IT-B2: Duplicate busID is rejected and file is unchanged")
    void testIT_B2_DuplicateBusRejected() throws IOException {
        Bus bus1 = makebus("12345678");
        repo.add(bus1);

        // Attempt to add a second bus with the same ID
        Bus bus2 = makebus("12345678");
        assertThrows(IllegalArgumentException.class, () -> repo.add(bus2));

        // File should still contain exactly 1 record
        assertEquals(1, repo.count(), "File should still have only 1 bus record");
    }

    /**
     * IT-B2: A bus with an invalid busID (non-digit characters) should throw
     * at construction time and never reach the repository.
     */
    @Test
    @Order(3)
    @DisplayName("IT-B2: Bus with non-digit ID is rejected at construction")
    void testIT_B2_InvalidBusIDNotStored() throws IOException {
        // Invalid ID — contains letters
        assertThrows(IllegalArgumentException.class,
            () -> new Bus("BUS12345", 40, 75.0, "Diesel"));

        // Repository should remain empty
        assertEquals(0, repo.count());
    }

    // =========================================================================
    // IT-B3: Updates are persisted correctly to the TXT file
    // =========================================================================

    /**
     * IT-B3: Updating a bus's fuel level and type should persist the changes.
     * The original values should no longer appear in the file.
     */
    @Test
    @Order(4)
    @DisplayName("IT-B3: Bus update is persisted correctly to TXT file")
    void testIT_B3_UpdatePersistedToFile() throws IOException {
        // Add the original bus
        Bus bus = new Bus("55667788", 40, 60.0, "Diesel");
        repo.add(bus);

        // Create an updated bus — capacity decreases (B2), fuel level changes
        Bus updated = new Bus("55667788", 35, 45.0, "Hybrid");
        repo.update(updated);

        // Retrieve and verify changes were persisted
        Bus retrieved = repo.retrieve("55667788");
        assertNotNull(retrieved);
        assertEquals(35, retrieved.getCapacity(),
            "Capacity should reflect the decrease");
        assertEquals(45.0, retrieved.getFuelLevel(), 0.001,
            "Fuel level should be updated");
        assertEquals("Hybrid", retrieved.getFuelType(),
            "Fuel type should be updated");
    }

    /**
     * IT-B3: Attempting to update a bus that does not exist should throw
     * and leave the file unchanged.
     */
    @Test
    @Order(5)
    @DisplayName("IT-B3: Updating non-existent bus throws and file is unchanged")
    void testIT_B3_UpdateNonExistentBus() throws IOException {
        repo.add(makebus("12345678"));
        int countBefore = repo.count();

        // Attempt to update a bus that doesn't exist
        Bus nonExistent = new Bus("99999999", 30, 50.0, "Diesel");
        assertThrows(IllegalArgumentException.class, () -> repo.update(nonExistent));

        // Count should be unchanged
        assertEquals(countBefore, repo.count());
    }

    // =========================================================================
    // IT-B4: Record counts are updated correctly
    // =========================================================================

    /**
     * IT-B4: Count should accurately reflect the number of buses added.
     */
    @Test
    @Order(6)
    @DisplayName("IT-B4: Count increases correctly as buses are added")
    void testIT_B4_CountUpdatesCorrectly() throws IOException {
        assertEquals(0, repo.count(), "Initial count should be 0");

        repo.add(makebus("11111111"));
        assertEquals(1, repo.count(), "Count should be 1 after first add");

        repo.add(makebus("22222222"));
        assertEquals(2, repo.count(), "Count should be 2 after second add");

        repo.add(makebus("33333333"));
        assertEquals(3, repo.count(), "Count should be 3 after third add");
    }

    /**
     * IT-B4: Count should remain unchanged after a failed add (duplicate bus ID).
     */
    @Test
    @Order(7)
    @DisplayName("IT-B4: Count does not change after rejected duplicate add")
    void testIT_B4_CountUnchangedAfterFailedAdd() throws IOException {
        repo.add(makebus("11111111"));
        int countBefore = repo.count();

        // Attempt to add a duplicate
        assertThrows(IllegalArgumentException.class,
            () -> repo.add(makebus("11111111")));

        assertEquals(countBefore, repo.count(),
            "Count should be unchanged after duplicate rejection");
    }
}
