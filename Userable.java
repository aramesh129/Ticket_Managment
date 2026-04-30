package sporting_event_ticketmanager.src;

import java.util.ArrayList;

/**
 * src.Userable
 *
 * An interface specifically made for the src.User class
 * outlines the functions that will be used in the specified
 * class
 *
 * @author Corin Withers, Lab Section 36
 *
 * @version November 6th, 2025
 *
 */
public interface Userable {
    /**
     * Accesses the tickets ArrayList to sort tickets by
     * date.
     */
    void sortTicketsByDate();

    /**
     * @param ticket src.Ticket to be added to the list
     * @return The removed ticket
     */
    boolean addTicket(Ticket ticket);

    /**
     * @param id The event id of the ticket
     * @param seatNumber The unique seat number
     * @return A boolean showing that the ticket was removed successfully
     */
    Ticket removeTicket(int id, String seatNumber);

    // All getters and setters
    String getUsername();
    void setUsername(String username);
    String getPassword();
    void setPassword(String password);
    String getFirstName();
    void setFirstName(String firstName);
    String getLastName();
    void setLastName(String lastName);
    static void setUserCount(int count) {

    }
    double getBalance();
    void setBalance(double balance);
    int getTicketCount();
    int getId();
    ArrayList<Ticket> getTickets();
}
