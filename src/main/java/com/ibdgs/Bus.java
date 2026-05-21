package com.ibdgs;

/** 
 * Enforces the following conditions:
 *   busID must be unique, exactly 8 digits (all numeric)
 *   capacity cannot increase during update operations
 *   drivers older than 50 cannot drive buses with capacity >= 50
 *   only drivers with >= 5 years experience can drive electric buses
 *   only Heavy or PublicTransport licence holders can drive electric or hybrid buses
 */
public class Bus {

    // unique identifier for the bus exactly 8 digits
    private String busID;

    // maximum passenger capacity of the bus
    private int capacity;

    // current fuel level as a percentage (0.0 to 100.0)
    private double fuelLevel;

    // fuel type: diesel, hybrid, electricity
    private String fuelType;

    /**
     * Constructs a new Bus after validating all fields.
     * @param busID     unique 8-digit bus ID
     * @param capacity  passenger capacity (must be positive)
     * @param fuelLevel current fuel level (0.0–100.0)
     * @param fuelType  fuel type (diesel, hybrid, electricity)
     * @throws IllegalArgumentException if any field fails validation
     */
    public Bus(String busID, int capacity, double fuelLevel, String fuelType) {
        validateBusID(busID);
        validateCapacity(capacity);
        validateFuelLevel(fuelLevel);
        validateFuelType(fuelType);

        this.busID    = busID;
        this.capacity = capacity;
        this.fuelLevel = fuelLevel;
        this.fuelType  = fuelType;
    }


    // Validation Methods
    /**
     * B1: Validates the bus ID format.
     *
     * @param id the bus ID to validate
     * @throws IllegalArgumentException 
     */
    public static void validateBusID(String id) {
        if (id == null || id.length() != 8) {
            throw new IllegalArgumentException(
                "B1: busID must be exactly 8 characters long.");
        }
        for (char c : id.toCharArray()) {
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException(
                    "B1: busID must contain only digits.");
            }
        }
    }

    /**
     * Validates that capacity is a positive integer.
     * @param capacity 
     * @throws IllegalArgumentException 
     */
    public static void validateCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be a positive integer.");
        }
    }

    /**
     * Validates that the fuel level is within the range 0.0–100.0.
     *
     * @param fuelLevel the fuel level to validate
     * @throws IllegalArgumentException if out of range
     */
    public static void validateFuelLevel(double fuelLevel) {
        if (fuelLevel < 0.0 || fuelLevel > 100.0) {
            throw new IllegalArgumentException("fuelLevel must be between 0.0 and 100.0.");
        }
    }

    /**
     * Validates that the fuel type is one of the accepted values.
     *
     * @param fuelType the fuel type to validate
     * @throws IllegalArgumentException if not a recognised fuel type
     */
    public static void validateFuelType(String fuelType) {
        if (fuelType == null) {
            throw new IllegalArgumentException("fuelType cannot be null.");
        }
        if (!fuelType.equals("Diesel") && !fuelType.equals("Hybrid")
                && !fuelType.equals("Electricity")) {
            throw new IllegalArgumentException(
                "fuelType must be one of: Diesel, Hybrid, Electricity.");
        }
    }

    /**
     * checks whether a driver is eligible to drive this bus based on age
     * Drivers older than 50 years cannot operate buses with capacity >= 50
     *
     * @param driverAge the driver's age in years
     * @throws IllegalArgumentException if the driver is over 50 and the bus capacity >= 50
     */
    public void checkDriverAgeRestriction(int driverAge) {
        if (driverAge > 50 && this.capacity >= 50) {
            throw new IllegalArgumentException(
                "B3: Drivers older than 50 cannot drive buses with a capacity of 50 or more.");
        }
    }

    /**
     * B4: Checks whether a driver has sufficient experience to drive an electric bus.
     * Only drivers with at least 5 years of experience can drive electric buses.
     *
     * @param experienceYears the driver's years of experience
     * @throws IllegalArgumentException if the bus is electric and driver has < 5 years experience
     */
    public void checkElectricExperienceRestriction(int experienceYears) {
        if (this.fuelType.equals("Electricity") && experienceYears < 5) {
            throw new IllegalArgumentException(
                "B4: Only drivers with at least 5 years of experience can drive electric buses.");
        }
    }

    /**
     * B5: Checks whether a driver holds the required licence to drive this bus.
     * Only Heavy or PublicTransport licence holders can operate electric or hybrid buses.
     *
     * @param licenseType the driver's licence type
     * @throws IllegalArgumentException if the licence is insufficient for this bus type
     */
    public void checkLicenceRestriction(String licenseType) {
        if ((this.fuelType.equals("Electricity") || this.fuelType.equals("Hybrid"))) {
            if (!licenseType.equals("Heavy") && !licenseType.equals("PublicTransport")) {
                throw new IllegalArgumentException(
                    "B5: Only Heavy or PublicTransport licence holders can drive electric or hybrid buses.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    /** Returns the bus's unique ID. */
    public String getBusID() { return busID; }

    /** Returns the bus capacity. */
    public int getCapacity() { return capacity; }

    /** Returns the current fuel level. */
    public double getFuelLevel() { return fuelLevel; }

    /** Returns the fuel type. */
    public String getFuelType() { return fuelType; }

    // -------------------------------------------------------------------------
    // Setters (with business rule enforcement)
    // -------------------------------------------------------------------------

    /**
     * B1: busID is immutable — this method always throws.
     *
     * @throws UnsupportedOperationException always
     */
    public void setBusID(String busID) {
        throw new UnsupportedOperationException(
            "B1: busID cannot be modified after creation.");
    }

    /**
     * B2: Updates the bus capacity.
     * Capacity can only decrease during an update — it cannot increase.
     *
     * @param newCapacity the new capacity
     * @throws IllegalArgumentException if the new capacity is greater than the current capacity
     */
    public void setCapacity(int newCapacity) {
        if (newCapacity > this.capacity) {
            throw new IllegalArgumentException(
                "B2: Bus capacity cannot increase during an update.");
        }
        validateCapacity(newCapacity);
        this.capacity = newCapacity;
    }

    /**
     * Updates the bus fuel level.
     *
     * @param fuelLevel the new fuel level (0.0–100.0)
     * @throws IllegalArgumentException if out of range
     */
    public void setFuelLevel(double fuelLevel) {
        validateFuelLevel(fuelLevel);
        this.fuelLevel = fuelLevel;
    }

    /**
     * Updates the bus fuel type.
     *
     * @param fuelType the new fuel type
     * @throws IllegalArgumentException if not a recognised fuel type
     */
    public void setFuelType(String fuelType) {
        validateFuelType(fuelType);
        this.fuelType = fuelType;
    }

    
    // returns a human-readable string representation of the bus.
    @Override
    public String toString() {
        return busID + "|" + capacity + "|" + fuelLevel + "|" + fuelType;
    }
}
