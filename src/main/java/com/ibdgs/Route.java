package com.ibdgs;

public class Route {
    // unique identifier for the route, with exactly 6 alphanumeric characters
    private String routeID;
 
    // total distance of the route in kilometres (must be positive)
    private double distance;
 
    // number of stops along the route (must be positive)
    private int numberOfStops;
 
    // route type: Normal or Express
    private String routeType;
 
    // whether the route is currently active
    private boolean active;
 
    // the ID of the bus currently assigned to this route, or null if unassigned
    private String assignedBusID;
 
    /**
     * Constructs a new Route after validating all fields.
     * The route begins with no bus assigned.
     *
     * @param routeID       unique 6-character alphanumeric route ID
     * @param distance    total route distance in kilometres 
     * @param numberOfStops number of stops on the route 
     * @param routeType     route type (Normal, Express)
     * @param active        whether the route is currently active
     * @throws IllegalArgumentException if any field fails validation
     */

    public Route(String routeID, double distance, int numberOfStops,
                 String routeType, boolean active) {
        validateRouteID(routeID);
        validateDistance(distance);
        validateNumberOfStops(numberOfStops);
        validateRouteType(routeType);
        checkStopCountForDistance(distance, numberOfStops);
        checkExpressStopLimit(routeType, numberOfStops);
 
        this.routeID        = routeID;
        this.distance     = distance;
        this.numberOfStops  = numberOfStops;
        this.routeType      = routeType;
        this.active         = active;
        this.assignedBusID  = null;
    }
 
 
    // -------------------------------------------------------------------------
    // Validation Methods
    // -------------------------------------------------------------------------
 
    /**
     * R1: Validates the route ID format.
     * The ID must be exactly 6 alphanumeric characters.
     *
     * @param id the route ID to validate
     * @throws IllegalArgumentException if the ID is null, not 6 characters, or contains
     *                                  non-alphanumeric characters
     */
    public static void validateRouteID(String id) {
        if (id == null || id.length() != 6) {
            throw new IllegalArgumentException(
                "R1: routeID must be exactly 6 characters long.");
        }
        for (char c : id.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                throw new IllegalArgumentException(
                    "R1: routeID must contain only alphanumeric characters.");
            }
        }
    }
 
    /**
     * Validates that the distance is a positive number.
     *
     * @param distance the distance to validate
     * @throws IllegalArgumentException if distance is not positive
     */

    public static void validateDistance(double distance) {
        if (distance <= 0.0) {
            throw new IllegalArgumentException(
                "The distance must be a positive number.");
        }
    }
 
    /**
     * Validates that the number of stops is a positive integer.
     *
     * @param numberOfStops the stop count to validate
     * @throws IllegalArgumentException if numberOfStops is not positive
     */
    public static void validateNumberOfStops(int numberOfStops) {
        if (numberOfStops <= 0) {
            throw new IllegalArgumentException(
                "The number of stops must be a positive integer.");
        }
    }
 
    /**
     * Validates that the route type is one of the accepted values.
     *
     * @param routeType the route type to validate
     * @throws IllegalArgumentException if not a recognised route type
     */
    public static void validateRouteType(String routeType) {
        if (routeType == null) {
            throw new IllegalArgumentException("The route type cannot be null.");
        }
        if (!routeType.equals("Normal") && !routeType.equals("Express")) {
            throw new IllegalArgumentException(
                "The route type must be normal or express.");
        }
    }
 
    /**
     * R3: Checks that routes longer than 100 km have at least 3 stops.
     *
     * @param distance    the route distance in kilometres
     * @param numberOfStops the number of stops on the route
     * @throws IllegalArgumentException if distance > 100 and numberOfStops < 3
     */
    public static void checkStopCountForDistance(double distance, int numberOfStops) {
        if (distance > 100.0 && numberOfStops < 3) {
            throw new IllegalArgumentException(
                "R3: Routes longer than 100 km must have at least 3 stops.");
        }
    }
 
    /**
     * R4: Checks that express routes do not exceed 5 stops.
     *
     * @param routeType     the type of the route
     * @param numberOfStops the number of stops on the route
     * @throws IllegalArgumentException if the route is Express and numberOfStops > 5
     */
    public static void checkExpressStopLimit(String routeType, int numberOfStops) {
        if (routeType != null && routeType.equals("Express") && numberOfStops > 5) {
            throw new IllegalArgumentException(
                "R4: Express routes cannot have more than 5 stops.");
        }
    }
 
    /**
     * R5: Checks whether a bus can be assigned to this route.
     * Inactive routes cannot be assigned a bus.
     *
     * @throws IllegalStateException if the route is not active
     */
    public void checkAssignmentEligibility() {
        if (!this.active) {
            throw new IllegalStateException(
                "R5: A bus cannot be assigned to an inactive route.");
        }
    }
 
 
    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------
 
    /** Returns the route's unique ID. */
    public String getRouteID() { 
        return routeID; 
    }
 
    /** Returns the route distance in kilometres. */
    public double getDistance() { 
        return distance; 
    }
 
    /** Returns the number of stops on the route. */
    public int getNumberOfStops() { 
        return numberOfStops; 
    }
 
    /** Returns the route type. */
    public String getRouteType() { 
        return routeType; 
    }
 
    /** Returns whether the route is currently active. */
    public boolean isActive() { 
        return active; 
    }
 
    /** Returns the ID of the bus currently assigned to this route, or null if none. */
    public String getAssignedBusID() { 
        return assignedBusID; 
    }
 
 
    // -------------------------------------------------------------------------
    // Setters (with business rule enforcement)
    // -------------------------------------------------------------------------
 
    /**
     * R1: routeID is immutable — this method always throws.
     *
     * @throws UnsupportedOperationException always
     */
    public void setRouteID(String routeID) {
        throw new UnsupportedOperationException(
            "R1: routeID cannot be modified after creation.");
    }
 
    /**
     * R2: Updates the route distance.
     * Distance can only increase during an update — it cannot decrease.
     *
     * @param newDistance the new distance in kilometres
     * @throws IllegalArgumentException if the new distance is less than the current distance
     */
    public void setDistance(double newDistance) {
        if (newDistance < this.distance) {
            throw new IllegalArgumentException(
                "R2: Route distance cannot decrease during an update.");
        }
        validateDistance(newDistance);
        checkStopCountForDistance(newDistance, this.numberOfStops);
        this.distance = newDistance;
    }
 
    /**
     * Updates the number of stops on the route.
     * Re-validates cross-field rules after the change.
     *
     * @param numberOfStops the new stop count
     * @throws IllegalArgumentException if the stop count violates any business rule
     */
    public void setNumberOfStops(int numberOfStops) {
        validateNumberOfStops(numberOfStops);
        checkStopCountForDistance(this.distance, numberOfStops);
        checkExpressStopLimit(this.routeType, numberOfStops);
        this.numberOfStops = numberOfStops;
    }
 
    /**
     * Updates the route type.
     * Re-validates cross-field rules after the change.
     *
     * @param routeType the new route type
     * @throws IllegalArgumentException if the type violates any business rule
     */
    public void setRouteType(String routeType) {
        validateRouteType(routeType);
        checkExpressStopLimit(routeType, this.numberOfStops);
        this.routeType = routeType;
    }
 
    /**
     * Updates the active status of the route.
     * If the route is deactivated, any assigned bus is automatically unassigned.
     *
     * @param active the new active status
     */
    public void setActive(boolean active) {
        if (!active) {
            this.assignedBusID = null;
        }
        this.active = active;
    }
 
    /**
     * R5: Assigns a bus to this route by its ID.
     * The route must be active before a bus can be assigned.
     *
     * @param busID the ID of the bus to assign
     * @throws IllegalStateException    if the route is inactive
     * @throws IllegalArgumentException if the bus ID is null or blank
     */
    public void assignBus(String busID) {
        checkAssignmentEligibility();
        if (busID == null || busID.isBlank()) {
            throw new IllegalArgumentException("busID cannot be null or blank.");
        }
        this.assignedBusID = busID;
    }
 
    /**
     * Removes the currently assigned bus from this route.
     * Has no effect if no bus is currently assigned.
     */
    public void unassignBus() {
        this.assignedBusID = null;
    }
 
 
    // returns a human-readable string representation of the route.
    @Override
    public String toString() {
        return routeID + "|" + distance + "|" + numberOfStops + "|"
                + routeType + "|" + active + "|"
                + (assignedBusID != null ? assignedBusID : "None");
    }

}
