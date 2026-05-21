package com.ibdgs;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DriverRepository {

    // Path to the TXT file used for persistent storage
    private final String filePath;

    /**
     * Constructs a DriverRepository using the specified file path.
     * Creates the file if it does not already exist.
     *
     * @param filePath path to the TXT storage file
     * @throws IOException if the file cannot be created
     */
    public DriverRepository(String filePath) throws IOException {
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
     * Adds a new driver to the repository.
     * The driver's ID must be unique; duplicate IDs are rejected.
     *
     * @param driver the Driver object to add
     * @throws IllegalArgumentException if a driver with the same ID already exists
     * @throws IOException              if the file cannot be written
     */
    public void add(Driver driver) throws IOException {
        // Check for duplicate driverID
        if (retrieve(driver.getDriverID()) != null) {
            throw new IllegalArgumentException(
                "D1: A driver with ID '" + driver.getDriverID() + "' already exists.");
        }

        // Append the driver record to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(driver.toString());
            writer.newLine();
        }
    }

    /**
     * Retrieves a driver by their unique ID.
     *
     * @param driverID the ID to search for
     * @return the matching Driver object, or null if not found
     * @throws IOException if the file cannot be read
     */
    public Driver retrieve(String driverID) throws IOException {
        // Read all lines and find the matching record
        List<String> lines = readAllLines();
        for (String line : lines) {
            if (line.isBlank()) continue;
            // Split into exactly 6 parts: driverID, name, experienceYears, licenseType, address, birthdate
            // Address is stored with commas replacing its internal pipes, so splitting on | is safe here
            String[] parts = line.split("\\|", 6);
            if (parts.length >= 1 && parts[0].equals(driverID)) {
                return parseDriver(parts);
            }
        }
        return null; // Not found
    }

    /**
     * Updates a driver's mutable fields (address, licenseType, experienceYears, birthdate).
     * Fields driverID and name are immutable and will be ignored (D5).
     * Licence type cannot be changed for drivers with > 10 years experience (D4).
     *
     * @param updatedDriver a Driver object containing the updated values
     * @throws IllegalArgumentException if the driver is not found
     * @throws IOException              if the file cannot be read or written
     */
    public void update(Driver updatedDriver) throws IOException {
        List<String> lines = readAllLines();
        boolean found = false;
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            if (line.isBlank()) {
                updatedLines.add(line);
                continue;
            }
            String[] parts = line.split("\\|", 6);
            if (parts[0].equals(updatedDriver.getDriverID())) {
                // Replace with updated driver record
                updatedLines.add(updatedDriver.toString());
                found = true;
            } else {
                updatedLines.add(line);
            }
        }

        if (!found) {
            throw new IllegalArgumentException(
                "Driver with ID '" + updatedDriver.getDriverID() + "' not found.");
        }

        // Write all lines back to the file
        writeAllLines(updatedLines);
    }

    /**
     * Returns the total number of driver records stored in the file.
     *
     * @return count of drivers
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
     * Parses a split line array into a Driver object.
     * Expected storage format: driverID|name|experienceYears|licenseType|address|birthdate
     *
     * The address is stored with commas as internal separators and must be
     * converted back to pipes (StreetNumber|StreetName|City|State|Country) on read.
     *
     * @param parts the array of fields from splitting a line with limit 6
     * @return the reconstructed Driver object
     */
    private Driver parseDriver(String[] parts) {
        String driverID      = parts[0];
        String name          = parts[1];
        int experienceYears  = Integer.parseInt(parts[2]);
        String licenseType   = parts[3];
        // Restore the address: commas back to pipes
        String address       = parts[4].replace(",", "|");
        String birthdate     = parts[5].trim();

        return new Driver(driverID, name, experienceYears, licenseType, address, birthdate);
    }

    /** Returns the file path used by this repository (for test teardown). */
    public String getFilePath() { return filePath; }
}