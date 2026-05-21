package com.ibdgs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BusRepository {

    // Path to the TXT file used for persistent storage
    private final String filePath;

    /**
     * Constructs a BusRepository using the specified file path.
     * Creates the file if it does not already exist.
     *
     * @param filePath path to the TXT storage file
     * @throws IOException if the file cannot be created
     */
    public BusRepository(String filePath) throws IOException {
        this.filePath = filePath;
        // Ensure the file exists; create it if not
        File file = new File(filePath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    // -------------------------------------------------------------------------
    // Core Operations
    // -------------------------------------------------------------------------

    /**
     * Adds a new bus to the repository.
     * The bus ID must be unique; duplicate IDs are rejected.
     *
     * @param bus the Bus object to add
     * @throws IllegalArgumentException if a bus with the same ID already exists
     * @throws IOException              if the file cannot be written
     */
    public void add(Bus bus) throws IOException {
        // Check for duplicate busID
        if (retrieve(bus.getBusID()) != null) {
            throw new IllegalArgumentException(
                "B1: A bus with ID '" + bus.getBusID() + "' already exists.");
        }

        // Append the bus record to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(bus.toString());
            writer.newLine();
        }
    }

    /**
     * Retrieves a bus by its unique ID.
     *
     * @param busID the ID to search for
     * @return the matching Bus object, or null if not found
     * @throws IOException if the file cannot be read
     */
    public Bus retrieve(String busID) throws IOException {
        List<String> lines = readAllLines();
        for (String line : lines) {
            if (line.isBlank()) continue;
            String[] parts = line.split("\\|");
            if (parts.length == 4 && parts[0].equals(busID)) {
                return parseBus(parts);
            }
        }
        return null; // Not found
    }

    /**
     * Updates an existing bus record with new field values.
     * Bus capacity can only stay the same or decrease (B2).
     * Bus ID is immutable and cannot be changed.
     *
     * @param updatedBus a Bus object with updated field values
     * @throws IllegalArgumentException if the bus is not found
     * @throws IOException              if the file cannot be read or written
     */
    public void update(Bus updatedBus) throws IOException {
        List<String> lines = readAllLines();
        boolean found = false;
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            if (line.isBlank()) {
                updatedLines.add(line);
                continue;
            }
            String[] parts = line.split("\\|");
            if (parts.length == 4 && parts[0].equals(updatedBus.getBusID())) {
                // Replace with updated bus record
                updatedLines.add(updatedBus.toString());
                found = true;
            } else {
                updatedLines.add(line);
            }
        }

        if (!found) {
            throw new IllegalArgumentException(
                "Bus with ID '" + updatedBus.getBusID() + "' not found.");
        }

        // Write all lines back to the file
        writeAllLines(updatedLines);
    }

    /**
     * Returns the total number of bus records stored in the file.
     *
     * @return count of buses
     * @throws IOException if the file cannot be read
     */
    public int count() throws IOException {
        List<String> lines = readAllLines();
        int count = 0;
        for (String line : lines) {
            if (!line.isBlank()) count++;
        }
        return count;
    }

    // -------------------------------------------------------------------------
    // Internal Helpers
    // -------------------------------------------------------------------------

    /**
     * Reads all lines from the storage file.
     *
     * @return list of lines
     * @throws IOException if the file cannot be read
     */
    private List<String> readAllLines() throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    /**
     * Overwrites the storage file with the given list of lines.
     *
     * @param lines the lines to write
     * @throws IOException if the file cannot be written
     */
    private void writeAllLines(List<String> lines) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    /**
     * Parses a split line array into a Bus object.
     * Expected format: busID|capacity|fuelLevel|fuelType
     *
     * @param parts the array of fields from splitting a line
     * @return the reconstructed Bus object
     */
    private Bus parseBus(String[] parts) {
        String busID    = parts[0];
        int capacity    = Integer.parseInt(parts[1]);
        double fuelLevel = Double.parseDouble(parts[2]);
        String fuelType = parts[3];
        return new Bus(busID, capacity, fuelLevel, fuelType);
    }

    /** Returns the file path used by this repository (for test teardown). */
    public String getFilePath() { return filePath; }
}
