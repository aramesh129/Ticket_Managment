package sporting_event_ticketmanager.src;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * Ticket
 *
 * Represents a ticket or a seat at a sporting event, which contains
 * data about the event's date, the type of seating, the seat location,
 * and the two pricing categories.
 *
 * @author Corin Withers, Lab Section 36
 *
 * @version November 6th, 2025
 *
 */
public class Ticket implements Datable, Ticketable, Serializable {
    private String seatNumber; // Seat number in the format "A1"
    private boolean taken; // Boolean for whether the ticket has been bought or not
    private String seatType; // "Away", "Home", or "undefined"
    private String selectedPricing; // "base", "preferred" or "undecided"
    private double basePrice; // base ticket pricing
    private double preferredPrice; // preferred ticket pricing
    private final int eventId; // Event id passed from SportingEvent
    private GregorianCalendar eventDate; // Date of the event

    /**
     * Basic constructor for the ticket class
     *
     * @param seatNumber identifier for the seat in the format "A1"
     * @param seatType "Home" or "Away" seating
     * @param eventDate Date of the event the ticket is for
     * @param eventId Numerical identification of the event
     * @param basePrice Base pricing for the ticket
     * @param preferredPrice Pricing for the ticket when the user chooses their seat
     */
    public Ticket(String seatNumber, String seatType, GregorianCalendar eventDate,
                  int eventId, double basePrice, double preferredPrice) {
        this.seatNumber = seatNumber; // Initialized in specified class
        // Whether the seat has been taken or not
        this.taken = false;
        // Starts as "undecided" is assigned to either "base" or "preferred"
        this.selectedPricing = "undecided";
        // Can only be "Home", "Away", or "undefined", should only
        // be "Home" or "Away", but "undefined" is for when someone tries
        // to incorrectly create a Ticket
        if (seatType.equals("Home") || seatType.equals("Away")) {
            this.seatType = seatType;
        } else {
            this.seatType = "undefined";
        }
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.basePrice = basePrice;
        this.preferredPrice = preferredPrice;
    }


    /**
     * Passes in all fields and initialize base price as 10, and preferred price as 15.
     *
     * @param seatNumber identifier for the seat in the format "A1"
     * @param seatType "Home" or "Away" seating
     * @param eventDate Date of the event the ticket is for
     * @param eventId Numerical identification of the event
     */
    public Ticket(String seatNumber, String seatType, GregorianCalendar eventDate, int eventId) {
        this(seatNumber, seatType, eventDate, eventId, 10.0, 15.0);
    }


    // Getters and setters for all fields.
    public GregorianCalendar getDate() {
        return eventDate;
    }

    public void setDate(GregorianCalendar date) {
        this.eventDate = date;
    }

    public String getSeatNumber() {
        return seatNumber;
    }
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isTaken() {
        return taken;
    }

    public void setTaken(boolean taken) {
        this.taken = taken;
    }

    public String getSeatType() {
        return seatType;
    }

    public void setSeatType(String seatType) {
        if (seatType.equals("Home") || seatType.equals("Away")) {
            this.seatType = seatType;
        } else {
            this.seatType = "undefined";
        }
    }
    public int getEventId() {
        return eventId;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public double getPreferredPrice() {
        return preferredPrice;
    }

    public void setPreferredPrice(double preferredPrice) {
        this.preferredPrice = preferredPrice;
    }

    public String getSelectedPricing() {
        return selectedPricing;
    }

    public void setSelectedPricing(String selectedPricing) {
        this.selectedPricing = selectedPricing;
    }

    /**
     * Outputs a string showing the details of the Ticket
     *
     * @return A string with details about the ticket
     */
    public String toString() {
        String finalString = "";
        finalString += String.format("Seat Number: %s\n", seatNumber);
        finalString += String.format("Seat Type: %s\n", seatType);
        finalString += String.format("Base Price: %.2f\n", basePrice);
        finalString += String.format("Preferred Price: %.2f\n", preferredPrice);
        finalString += String.format("Event Id: %d\n", eventId);
        finalString += String.format("Event Date: %s\n", eventDate.getTime());
        finalString += String.format("Is the seat taken: %b\n", taken);
        return finalString;
    }
}
