package sporting_event_ticketmanager.src;

import java.io.Serializable;
import java.util.GregorianCalendar;

/**
 * SportingEvent
 *
 * Represents a sporting event between two different teams
 * has information about the teams, the date of the event,
 * the pricing of tickets, the availability of tickets,
 * and the layout of the seating.
 *
 *
 * @version November 6th, 2025
 *
 */
public class SportingEvent implements Datable, Eventable, Serializable {
    private String eventName; // Name of the event
    private String homeTeam; // Name of the home team
    private String awayTeam; // Name of the away team
    private GregorianCalendar date; // Date of the event
    private double basePrice; // Price of first available tickets
    private double preferredPrice; // Price of preferred seating.
    private int homeTicketsRemaining; // Number of home tickets remaining
    private int awayTicketsRemaining; // Number of away tickets remaining
    private int id; // Numerical event ID
    private Ticket[][] homeTickets; // Array representing the available home seats
    private Ticket[][] awayTickets; // Array representing the available away seats
    private static int eventCount = 0; // Total count of events
    private static final Object LOCK = new Object(); // Static LOCK for synchronization


    /**
     * Basic constructor for the SportingEvent class
     *
     * @param eventName Name of the event
     * @param homeTeam Name of the home team
     * @param awayTeam Name of the away team
     * @param date Date of the sporting event
     * @param basePrice Base pricing for this event's tickets
     * @param preferredPrice Pricing for the event's tickets when the user chooses their seat
     */
    public SportingEvent(String eventName, String homeTeam, String awayTeam, GregorianCalendar date,
                         double basePrice, double preferredPrice) {
        this.eventName = eventName;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.date = date;
        this.basePrice = basePrice;
        this.preferredPrice = preferredPrice;
        // Initializes the arrays to represent a 10 by 20 grid each section
        // having 200 seats.
        this.homeTickets = new Ticket[10][20];
        this.awayTickets = new Ticket[10][20];
        this.homeTicketsRemaining = 10 * 20;
        this.awayTicketsRemaining = 10 * 20;

        this.id = eventCount;

        // Safely increments the event count so no repeat ids occur
        synchronized (LOCK) {
            eventCount++;
        }

        // For loop that fills the ticket arrays
        for (int i = 0; i < 10; i++) {
            char beginningLetter = (char)('A' + i);
            for (int j = 0; j < 20; j++) {
                this.homeTickets[i][j] = new Ticket("" + beginningLetter + j,
                        "Home", date, id, basePrice, preferredPrice);
                this.awayTickets[i][j] = new Ticket("" + beginningLetter + j,
                        "Away", date, id, basePrice, preferredPrice);
            }
        }
    }

    /**
     * If enough home tickets are remaining "buy" the first available tickets
     * putting them into a ticket array and returning that array. Increments
     * through the list of tickets util it lands on an available ticket, which
     * is added to the ticket array and is repeated for each party member.
     *
     * @param partySize The size of the party
     * @return An array of tickets that have just been purchased
     */
    public synchronized Ticket[] buyHomeTickets(int partySize) {
        if (partySize <= 0) {
            return null;
        }
        Ticket[] boughtTickets = new Ticket[partySize];
        int count = 0;
        if (partySize > 0 && partySize <= homeTicketsRemaining) {
            for (int i = 0; i < homeTickets.length; i++) {
                for (int j = 0; j < homeTickets[i].length; j++) {
                    boolean correctType = homeTickets[i][j].getSeatType().equals("Home");
                    if (homeTickets[i][j] != null && !homeTickets[i][j].isTaken() && correctType) {
                        homeTickets[i][j].setTaken(true);
                        homeTickets[i][j].setSelectedPricing("base");
                        boughtTickets[count] = homeTickets[i][j];
                        count++;
                        homeTicketsRemaining--;
                    }
                    if (count >= boughtTickets.length) {
                        break;
                    }
                }
                if (count >= boughtTickets.length) {
                    break;
                }
            }
        } else {
            return null;
        }

        return boughtTickets;
    }

    /**
     * If enough away tickets are remaining "buy" the first available tickets
     * putting them into a ticket array and returning that array. Increments
     * through the list of tickets util it lands on an available ticket, which
     * is added to the ticket array and is repeated for each party member.
     *
     * @param partySize The size of the party
     * @return An array of tickets that have just been purchased
     */
    public synchronized Ticket[] buyAwayTickets(int partySize) {
        if (partySize <= 0) {
            return null;
        }
        Ticket[] boughtTickets = new Ticket[partySize];
        int count = 0;
        if (partySize > 0 && partySize <= awayTicketsRemaining) {
            for (int i = 0; i < awayTickets.length; i++) {
                for (int j = 0; j < awayTickets[i].length; j++) {
                    boolean correctType = awayTickets[i][j].getSeatType().equals("Away");
                    if (awayTickets[i][j] != null && !awayTickets[i][j].isTaken() && correctType) {
                        awayTickets[i][j].setTaken(true);
                        awayTickets[i][j].setSelectedPricing("base");
                        boughtTickets[count] = awayTickets[i][j];
                        count++;
                        awayTicketsRemaining--;
                    }
                    if (count >= boughtTickets.length) {
                        break;
                    }
                }
                if (count >= boughtTickets.length) {
                    break;
                }
            }
        } else {
            return null;
        }

        return boughtTickets;
    }

    /**
     * Depending on the seat type it increments through the corresponding array
     * until it hits a ticket with a seat number that is equal to the one in the provided
     *
     * @param seatNumber The targeted seat
     * @param seatType "Home" and "Away"
     * @return The ticket or null if the ticket is not available
     */
    public synchronized Ticket buyPreferredTicket(String seatNumber, String seatType) {
        switch (seatType) {
            case "Home":
                for (int i = 0; i < homeTickets.length; i++) {
                    for (int j = 0; j < homeTickets[i].length; j++) {
                        if (homeTickets[i][j].getSeatNumber().equals(seatNumber) &&
                            !homeTickets[i][j].isTaken()) {
                            homeTickets[i][j].setTaken(true);
                            homeTickets[i][j].setSelectedPricing("preferred");
                            homeTicketsRemaining--;
                            return homeTickets[i][j];
                        }
                    }
                }
                break;
            case "Away":
                for (int i = 0; i < awayTickets.length; i++) {
                    for (int j = 0; j < awayTickets[i].length; j++) {
                        if (awayTickets[i][j].getSeatNumber().equals(seatNumber) &&
                                !awayTickets[i][j].isTaken()) {
                            awayTickets[i][j].setTaken(true);
                            awayTickets[i][j].setSelectedPricing("preferred");
                            awayTicketsRemaining--;
                            return awayTickets[i][j];
                        }
                    }
                }
                break;
        }

        System.out.println("Preferred Seating is Unavailable!");
        return null;
    }


    //

    /**
     * A method that checks if an event occurs sometime between today and
     * a week from now.
     *
     * @return A boolean affirming whether the event is within a week or not
     */
    public boolean isWithinWeek() {
        GregorianCalendar today = new GregorianCalendar();
        GregorianCalendar nextWeek = new GregorianCalendar(today.get(GregorianCalendar.YEAR),
                today.get(GregorianCalendar.MONTH), today.get(GregorianCalendar.DAY_OF_MONTH) + 7);
        return (date.after(today) && date.before(nextWeek));
    }

    //

    /**
     * Blocks out a rectangular section by setting the tickets taken value to true.
     * Takes in the starting and ending row, represented by a character, and a
     * starting and ending row represented by an integer. If a seat number
     * has components between or equal to the start and end value
     *
     * @param startRow The starting row for the block out
     * @param startColumn The starting column for the block out
     * @param endRow The ending row for the block out
     * @param endColumn The ending column for the block out
     * @param seatType "Home" or "Away"
     */
    public void blockOutSection(char startRow, int startColumn, char endRow,
                                int endColumn, String seatType) {
        switch (seatType) {
            case "Home":
                for (int i = 0; i < homeTickets.length; i++) {
                    for (int j = 0; j < homeTickets[i].length; j++) {
                        char rowChar = homeTickets[i][j].getSeatNumber().charAt(0);
                        int colNum = Integer.parseInt(homeTickets[i][j].getSeatNumber().substring(1));
                        boolean betweenRow = (rowChar >= startRow && rowChar <= endRow);
                        boolean betweenCol = (colNum >= startColumn && colNum <= endColumn);
                        if (betweenRow && betweenCol) {
                            homeTickets[i][j].setTaken(true);
                            homeTicketsRemaining--;
                        }
                    }
                }
                break;
            case "Away":
                for (int i = 0; i < awayTickets.length; i++) {
                    for (int j = 0; j < awayTickets[i].length; j++) {
                        char rowChar = awayTickets[i][j].getSeatNumber().charAt(0);
                        int colNum = Integer.parseInt(awayTickets[i][j].getSeatNumber().substring(1));
                        boolean betweenRow = (rowChar >= startRow && rowChar <= endRow);
                        boolean betweenCol = (colNum >= startColumn && colNum <= endColumn);
                        if (betweenRow && betweenCol) {
                            awayTickets[i][j].setTaken(true);
                            awayTicketsRemaining--;
                        }
                    }
                }
        }
    }

    /**
     * If the id of the ticket does not
     * equal the id of the event, stop the method. Otherwise, increment
     * through the array corresponding with the ticket's seat type, until
     * a ticket is found that is the same as the ticket provided in the
     * parameter. After that, reset the ticket so it is now available for
     * purchase.
     *
     * @param ticket A ticket for the event
     */
    public void returnTicket(Ticket ticket) {
        String seatType = ticket.getSeatType();
        if (ticket.getEventId() != this.id) {
            System.out.println("This ticket isn't for this event!");
            return;
        }

        switch (seatType) {
            case "Home":
                for (int i = 0; i < homeTickets.length; i++) {
                    for (int j = 0; j < homeTickets[i].length; j++) {
                        if (homeTickets[i][j].equals(ticket)) {
                            homeTickets[i][j].setTaken(false);
                            homeTickets[i][j].setSelectedPricing("undecided");
                            homeTicketsRemaining++;
                        }
                    }
                }
                break;
           case "Away":
               for (int i = 0; i < awayTickets.length; i++) {
                   for (int j = 0; j < awayTickets[i].length; j++) {
                       if (awayTickets[i][j].equals(ticket)) {
                           awayTickets[i][j].setTaken(false);
                           awayTickets[i][j].setSelectedPricing("undecided");
                           awayTicketsRemaining++;
                       }
                   }
               }
               break;
        }
    }

    /**
     * Returns a string representing the seating grid and showing open and
     * closed seats
     *
     * @param seatType "Home" or "Away"
     * @return A string showing the open seats in the specified grid
     */
    public String gridString(String seatType) {
        Ticket[][] grid = null;
        String gridString = " ";

        for (int i = 0; i < homeTickets[0].length; i++) {
            gridString += " " + i;
        }

        gridString += "\n";

        switch (seatType) {
            case "Home":
                grid = homeTickets;
                break;
            case "Away":
                grid = awayTickets;
                break;
            default:
                return "Invalid seat type!";
        }

        for (int i = 0; i < grid.length; i++) {
            gridString += "" + (char)('A' + i);
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j].isTaken()) {
                    gridString += " X";
                } else {
                    gridString += " O";
                }
            }
            gridString += "\n";
        }
        return gridString;
    }

    // Collection of getters and setters for the private fields of this class
    public String getEventName() {
        return eventName;
    }

    public String getHomeTeam() {
        return homeTeam;
    }
    public String getAwayTeam() {
        return awayTeam;
    }
    public GregorianCalendar getDate() {
        return date;
    }

    public int getHomeTicketsRemaining() {
        return homeTicketsRemaining;
    }

    public int getAwayTicketsRemaining() {
        return awayTicketsRemaining;
    }

    public int getId() {
        return id;
    }

    public Ticket[][] getHomeTickets() {
        return homeTickets;
    }

    public Ticket[][] getAwayTickets() {
        return awayTickets;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    public int getEventCount() {
        return eventCount;
    }

    public synchronized void setEventCount(int eventCount) {
        this.eventCount = eventCount;
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
}
