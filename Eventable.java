package sporting_event_ticketmanager.src;

/**
 * Eventable
 *
 * An interface specifically made for the SportingEvent class
 * outlines the functions that will be used in the specified
 * class
 *
 * @author Corin Withers, Lab Section 36
 *
 * @version November 6th, 2025
 *
 */
public interface Eventable {
    /**
     * @param partySize The size of the party
     * @return An array of tickets that have just been purchased
     */
    Ticket[] buyHomeTickets(int partySize);

    /**
     * @param partySize The size of the party
     * @return An array of tickets that have just been purchased
     */
    Ticket[] buyAwayTickets(int partySize);

    /**
     * @param seatNumber The targeted seat
     * @param seatType "Home" and "Away"
     * @return The ticket or null if the ticket is not available
     */
    Ticket buyPreferredTicket(String seatNumber, String seatType);

    /**
     * @param startRow The starting row for the block out
     * @param startColumn The starting column for the block out
     * @param endRow The ending row for the block out
     * @param endColumn The ending column for the block out
     * @param seatType "Home" or "Away"
     */
    void blockOutSection(char startRow, int startColumn, char endRow, int endColumn, String seatType);

    /**
     * @return A boolean affirming whether the event is within a week or not
     */
    boolean isWithinWeek();

    /**
     * @param ticket A ticket for the event
     */
    void returnTicket(Ticket ticket);

    // All getters and setters for the class to implement
    int getHomeTicketsRemaining();
    int getAwayTicketsRemaining();
    int getId();
    Ticket[][] getHomeTickets();
    Ticket[][] getAwayTickets();
    String getEventName();
    String getHomeTeam();
    String getAwayTeam();
    void setEventCount(int eventCount);
    int getEventCount();
    void setEventName(String eventName);
    void setHomeTeam(String homeTeam);
    void setAwayTeam(String awayTeam);
    double getBasePrice();
    void setBasePrice(double basePrice);
    double getPreferredPrice();
    void setPreferredPrice(double preferredPrice);

}
