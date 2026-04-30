package sporting_event_ticketmanager.src;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * User
 *
 * Represents a user of the SportingEvent app, contains information
 * about the user's balance, username, password, first name, last name,
 * and the tickets they have previously bought.
 *
 *
 * @version November 8th, 2025
 *
 */
public class User implements Userable, Serializable {
    private static int userCount = 0; // Total user count for determining id
    private final int id; // src.User id
    private String username; // Unique username used for login
    private String password; // Password
    private String firstName; // src.User's first name
    private String lastName; // src.User's last name
    private double balance; // src.User's balance
    private int ticketCount; // Amount of tickets the user has
    private ArrayList<Ticket> tickets; // The user's tickets
    private static final Object LOCK = new Object(); // Lock for synchronization

    /**
     * Basic constructor for the src.User class. Takes in username, password, first name and last name
     * all fields are strings.
     *
     * @param username src.User's unique username
     * @param password src.User's password key
     * @param firstName src.User's first name
     * @param lastName src.User's last name
     */
    public User(String username, String password, String firstName, String lastName) {
        // Safely adds to the user count so two users don't have the same id.
        synchronized (LOCK) {
            this.userCount++;
        }
        this.id = userCount;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        // Initializes ticket count to 0 since new users shouldn't have any tickets
        this.ticketCount = 0;
        // Initializes balance to 0.0 since new users haven't added money to their accounts
        // yet
        this.balance = 0.0;
        this.tickets = new ArrayList<>();
    }

    //

    /**
     * Sorts the tickets in the tickets array list by date, from earliest to
     * latest. Uses bubble sort to compare two tickets dates, and swap them
     * if one comes before the other. Increment through the list and repeat
     * until no swaps occur.
     */
    public synchronized void sortTicketsByDate() {
        int swaps = 0;
        do {
            swaps = 0;
            for (int i = 0; i < this.tickets.size() - 1; i++) {
                Ticket t1 = this.tickets.get(i);
                Ticket t2 = this.tickets.get(i + 1);
                if (t1.getDate().after(t2.getDate())) {
                    this.tickets.set(i, t2);
                    this.tickets.set(i + 1, t1);
                    swaps++;
                }
            }
        } while (swaps > 0);
    }

    /**
     * Adds a ticket to the end of the array list, only if the price of the ticket
     * is under the balance for the user, first if statement determines which
     * price the ticket will use when comparing to balance.
     *
     * @param ticket src.Ticket to be added to the list
     * @return A boolean showing whether the add ticket operation completed successfully
     */
    public synchronized boolean addTicket(Ticket ticket) {
        double ticketPrice;
        if (ticket.getSelectedPricing().equals("base")) {
            ticketPrice = ticket.getBasePrice();
        } else if (ticket.getSelectedPricing().equals("preferred")) {
            ticketPrice = ticket.getPreferredPrice();
        } else {
            return false;
        }

        if (ticketPrice <= this.balance) {
            this.tickets.add(ticket);
            this.ticketCount++;
            this.balance -= ticketPrice;
        } else {
            return false;
        }

        return true;
    }

    /**
     * Increments through the array list until it finds a ticket with an identical
     * id and seat number, returns the money the user spent on the
     * ticket to their balance, and then remove the ticket from the list
     *
     *
     * @param eventID The event id of the ticket
     * @param seatNumber The unique seat number
     * @return The removed ticket
     */
    public synchronized Ticket removeTicket(int eventID, String seatNumber) {
        double ticketPrice;
        for (int i = 0; i < this.tickets.size(); i++) {
            Ticket t1 = this.tickets.get(i);
            if (eventID == t1.getEventId() && seatNumber.equals(t1.getSeatNumber())) {
                if (t1.getSelectedPricing().equals("base")) {
                    ticketPrice = t1.getBasePrice();
                } else if (t1.getSelectedPricing().equals("preferred")) {
                    ticketPrice = t1.getPreferredPrice();
                } else {
                    return null;
                }
                this.balance += ticketPrice;
                this.tickets.remove(i);
                this.ticketCount--;
                return t1;
            }
        }
        return null;
    }

    // Basic getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // method to set userCount to current highest after loading database
    // used so even when the program is terminated and run again it will
    // still have the correct userCount after reading database
    public static void setUserCount(int count) {
        synchronized (LOCK) {
            userCount = count;
        }
    }


    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public int getTicketCount() {
        return ticketCount;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Ticket> getTickets() {
        return tickets;
    }
}
