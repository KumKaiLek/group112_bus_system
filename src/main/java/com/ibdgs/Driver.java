package com.ibdgs;

/**
 * Represents a bus driver in the Intelligent Bus Driver Guidance System.
 *
 * Enforces the following conditions:
 *   D1 - driverID must be unique, exactly 10 chars:
 *        first 2 chars are digits 2–9,
 *        at least 2 special characters between positions 3–8,
 *        last 2 chars are uppercase letters A–Z.
 *   D2 - Address format: StreetNumber|StreetName|City|State|Country
 *   D3 - Birthdate format: DD-MM-YYYY
 *   D4 - licenseType cannot be changed if experienceYears > 10
 *   D5 - driverID and name are immutable after creation
 */
public class Driver {

    // Unique identifier for the driver (immutable after creation)
    private String driverID;

    // Full name of the driver (immutable after creation)
    private String name;

    // Number of years the driver has been licensed
    private int experienceYears;

    // Type of licence: Light, Medium, Heavy, PublicTransport
    private String licenseType;

    // Address in format: StreetNumber|StreetName|City|State|Country
    private String address;

    // Date of birth in format: DD-MM-YYYY
    private String birthdate;

    /**
     * Constructs a new Driver after validating all fields.
     *
     * @param driverID       unique 10-character driver ID
     * @param name           full name of the driver
     * @param experienceYears years of driving experience
     * @param licenseType    licence type (Light, Medium, Heavy, PublicTransport)
     * @param address        address in StreetNumber|StreetName|City|State|Country format
     * @param birthdate      date of birth in DD-MM-YYYY format
     * @throws IllegalArgumentException if any field fails validation
     */
    public Driver(String driverID, String name, int experienceYears,
                  String licenseType, String address, String birthdate) {

        // Validate all fields before assigning
        validateDriverID(driverID);
        validateAddress(address);
        validateBirthdate(birthdate);
        validateLicenseType(licenseType);

        this.driverID = driverID;
        this.name = name;
        this.experienceYears = experienceYears;
        this.licenseType = licenseType;
        this.address = address;
        this.birthdate = birthdate;
    }

    // -------------------------------------------------------------------------
    // Validation Methods
    // -------------------------------------------------------------------------

    /**
     * D1: Validates the driver ID format.
     * - Exactly 10 characters long
     * - First 2 characters must be digits between 2 and 9
     * - At least 2 special characters in positions 3–8 (index 2–7)
     * - Last 2 characters must be uppercase letters A–Z
     *
     * @param id the driver ID to validate
     * @throws IllegalArgumentException if the ID does not meet requirements
     */
    public static void validateDriverID(String id) {
        if (id == null || id.length() != 10) {
            throw new IllegalArgumentException(
                "D1: driverID must be exactly 10 characters long.");
        }

        // First two characters must be digits 2–9
        char c0 = id.charAt(0);
        char c1 = id.charAt(1);
        if (!Character.isDigit(c0) || c0 < '2' || c0 > '9') {
            throw new IllegalArgumentException(
                "D1: First character must be a digit between 2 and 9.");
        }
        if (!Character.isDigit(c1) || c1 < '2' || c1 > '9') {
            throw new IllegalArgumentException(
                "D1: Second character must be a digit between 2 and 9.");
        }

        // Count special characters in positions 3–8 (index 2–7)
        int specialCount = 0;
        for (int i = 2; i <= 7; i++) {
            char c = id.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                specialCount++;
            }
        }
        if (specialCount < 2) {
            throw new IllegalArgumentException(
                "D1: driverID must have at least 2 special characters between positions 3 and 8.");
        }

        // Last two characters must be uppercase letters A–Z
        char c8 = id.charAt(8);
        char c9 = id.charAt(9);
        if (!Character.isUpperCase(c8) || !Character.isLetter(c8)) {
            throw new IllegalArgumentException(
                "D1: Second-to-last character must be an uppercase letter.");
        }
        if (!Character.isUpperCase(c9) || !Character.isLetter(c9)) {
            throw new IllegalArgumentException(
                "D1: Last character must be an uppercase letter.");
        }
    }

    /**
     * D2: Validates that the address follows the format:
     *     StreetNumber|StreetName|City|State|Country
     *
     * @param address the address string to validate
     * @throws IllegalArgumentException if the format is incorrect
     */
    public static void validateAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException(
                "D2: Address cannot be null or blank.");
        }
        String[] parts = address.split("\\|");
        if (parts.length != 5) {
            throw new IllegalArgumentException(
                "D2: Address must follow format: StreetNumber|StreetName|City|State|Country");
        }
        // Each part must be non-empty
        for (String part : parts) {
            if (part.isBlank()) {
                throw new IllegalArgumentException(
                    "D2: All address components must be non-empty.");
            }
        }
    }

    /**
     * D3: Validates that the birthdate follows the format DD-MM-YYYY.
     *
     * @param birthdate the birthdate string to validate
     * @throws IllegalArgumentException if the format is incorrect
     */
    public static void validateBirthdate(String birthdate) {
        if (birthdate == null) {
            throw new IllegalArgumentException("D3: Birthdate cannot be null.");
        }
        // Must match exactly DD-MM-YYYY
        if (!birthdate.matches("\\d{2}-\\d{2}-\\d{4}")) {
            throw new IllegalArgumentException(
                "D3: Birthdate must follow the format DD-MM-YYYY.");
        }

        // Basic range checks
        int day   = Integer.parseInt(birthdate.substring(0, 2));
        int month = Integer.parseInt(birthdate.substring(3, 5));
        int year  = Integer.parseInt(birthdate.substring(6, 10));

        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("D3: Day must be between 01 and 31.");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("D3: Month must be between 01 and 12.");
        }
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException("D3: Year must be between 1900 and 2100.");
        }
    }

    /**
     * Validates that the licenseType is one of the accepted values.
     *
     * @param licenseType the licence type to validate
     * @throws IllegalArgumentException if the licence type is not recognised
     */
    public static void validateLicenseType(String licenseType) {
        if (licenseType == null) {
            throw new IllegalArgumentException("licenseType cannot be null.");
        }
        if (!licenseType.equals("Light") && !licenseType.equals("Medium")
                && !licenseType.equals("Heavy") && !licenseType.equals("PublicTransport")) {
            throw new IllegalArgumentException(
                "licenseType must be one of: Light, Medium, Heavy, PublicTransport.");
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** Returns the driver's unique ID. */
    public String getDriverID() { return driverID; }

    /** Returns the driver's full name. */
    public String getName() { return name; }

    /** Returns the driver's years of experience. */
    public int getExperienceYears() { return experienceYears; }

    /** Returns the driver's licence type. */
    public String getLicenseType() { return licenseType; }

    /** Returns the driver's address. */
    public String getAddress() { return address; }

    /** Returns the driver's birthdate. */
    public String getBirthdate() { return birthdate; }

    // -------------------------------------------------------------------------
    // Setters (with business rule enforcement)
    // -------------------------------------------------------------------------

    /**
     * D5: driverID is immutable — this method always throws.
     *
     * @throws UnsupportedOperationException always
     */
    public void setDriverID(String driverID) {
        throw new UnsupportedOperationException(
            "D5: driverID cannot be modified after creation.");
    }

    /**
     * D5: name is immutable — this method always throws.
     *
     * @throws UnsupportedOperationException always
     */
    public void setName(String name) {
        throw new UnsupportedOperationException(
            "D5: name cannot be modified after creation.");
    }

    /**
     * Updates the driver's years of experience.
     *
     * @param experienceYears new value (must be non-negative)
     * @throws IllegalArgumentException if value is negative
     */
    public void setExperienceYears(int experienceYears) {
        if (experienceYears < 0) {
            throw new IllegalArgumentException("experienceYears cannot be negative.");
        }
        this.experienceYears = experienceYears;
    }

    /**
     * D4: Updates the driver's licence type.
     * Cannot be changed if the driver has more than 10 years of experience.
     *
     * @param licenseType the new licence type
     * @throws IllegalStateException    if driver has more than 10 years of experience
     * @throws IllegalArgumentException if the licence type is invalid
     */
    public void setLicenseType(String licenseType) {
        if (this.experienceYears > 10) {
            throw new IllegalStateException(
                "D4: licenseType cannot be changed for drivers with more than 10 years of experience.");
        }
        validateLicenseType(licenseType);
        this.licenseType = licenseType;
    }

    /**
     * D2: Updates the driver's address after validating format.
     *
     * @param address the new address
     * @throws IllegalArgumentException if the format is invalid
     */
    public void setAddress(String address) {
        validateAddress(address);
        this.address = address;
    }

    /**
     * D3: Updates the driver's birthdate after validating format.
     *
     * @param birthdate the new birthdate
     * @throws IllegalArgumentException if the format is invalid
     */
    public void setBirthdate(String birthdate) {
        validateBirthdate(birthdate);
        this.birthdate = birthdate;
    }

    /**
     * Returns a human-readable string representation of the driver.
     * Used for TXT file storage.
     *
     * The address field uses commas as internal separators in storage
     * (e.g. "10,Swanston St,Melbourne,VIC,Australia") to avoid clashing
     * with the pipe delimiter used between fields on the line.
     */
    @Override
    public String toString() {
        // Replace internal address pipes with commas for safe storage
        String storedAddress = address.replace("|", ",");
        return driverID + "|" + name + "|" + experienceYears + "|"
             + licenseType + "|" + storedAddress + "|" + birthdate;
    }
}