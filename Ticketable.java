package sporting_event_ticketmanager.src;

/**
 * Ticketable
 *
 * An interface specifically made for the Ticket class
 * outlines the functions that will be used in the specified
 * class.
 *
 *
 * @version November 6th, 2025
 *
 */
public interface Ticketable {
    // All getters and setters
    String getSeatNumber();
    String getSeatType();
    boolean isTaken();
    void setTaken(boolean taken);
    void setSeatNumber(String seatNumber);
    void setSeatType(String seatType);
    double getBasePrice();
    void setBasePrice(double basePrice);
    double getPreferredPrice();
    void setPreferredPrice(double preferredPrice);
    String getSelectedPricing();
    void setSelectedPricing(String selectedPricing);
    int getEventId();
    String toString();
}
