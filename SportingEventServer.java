package sporting_event_ticketmanager.src;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Handles client connections and manages all server-side logic for
 * the Sporting Event ticket application.
 *
 * @version 11/23/2025
 */
public class SportingEventServer extends Thread implements SportingEventServerInterface {
    private Socket socket;
    private static final UserDatabase USER_DATABASE = new UserDatabase();
    private static final SportingEventDatabase SPORTING_EVENT_DATABASE = new SportingEventDatabase();

    public SportingEventServer(Socket socket) {
        this.socket = socket;
    }

    /**
     * Main loop for handling client commands after connection
     */
    @Override
    public void run() {
        try (
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())
        ) {
            boolean clientConnected = true;

            while (clientConnected) {
                boolean loggedIn = false;
                boolean isAdmin = false;
                User currentUser = null;

                //Loop to handle login/signup until successful
                while (!loggedIn) {
                    try {
                        String command = (String) ois.readObject();

                        if ("login".equalsIgnoreCase(command)) {
                            String username = (String) ois.readObject();
                            String password = (String) ois.readObject();

                            if ("admin".equals(username) && "admin".equals(password)) {
                                oos.writeBoolean(true);
                                oos.flush();
                                oos.writeBoolean(true);
                                oos.flush();
                                loggedIn = true;
                                isAdmin = true;
                            } else if (USER_DATABASE.validateUser(username, password)) {
                                oos.writeBoolean(true);
                                oos.flush();
                                oos.writeBoolean(false);
                                oos.flush();
                                currentUser = USER_DATABASE.getUserByUsername(username);
                                loggedIn = true;
                                isAdmin = false;
                            } else {
                                oos.writeBoolean(false);
                                oos.flush();
                            }
                        } else if ("signup".equalsIgnoreCase(command)) {
                            String firstName = (String) ois.readObject();
                            String lastName = (String) ois.readObject();
                            String password = (String) ois.readObject();
                            String username = (String) ois.readObject();

                            // Check if taken
                            boolean isTaken = (USER_DATABASE.getUserByUsername(username) != null);
                            oos.writeBoolean(isTaken);
                            oos.flush();
                            if (isTaken) continue;

                            //Create new user
                            User newUser = new User(username, password, firstName, lastName);
                            if (USER_DATABASE.addUser(newUser)) {
                                currentUser = newUser;
                                loggedIn = true;
                                isAdmin = false;
                                oos.writeBoolean(false);
                                oos.flush();
                            }
                        } else {
                            oos.writeBoolean(false);
                            oos.flush();
                        }
                    } catch (EOFException e) {
                        clientConnected = false;
                        break;
                    }
                }

                if (!clientConnected) break;

                if (isAdmin) {
                    while (loggedIn) {
                        try {
                            int choice = ois.readInt();
                            switch (choice) {
                                case 1:
                                    handleLockout(ois, oos);
                                    break;
                                case 2:
                                    handleSetPrices(ois, oos);
                                    break;
                                case 3:
                                    handleCreateEvent(ois, oos);
                                    break;
                                case 4:
                                    loggedIn = false;
                                    break;
                            }
                        } catch (Exception e) {
                            loggedIn = false;
                            clientConnected = false;
                        }
                    }
                } else {
                    //Main user menu loop
                    while (loggedIn) {
                        try {
                            currentUser = USER_DATABASE.getUserByUsername(currentUser.getUsername());
                            oos.writeObject(currentUser.getFirstName());
                            oos.flush();
                            oos.writeObject(currentUser.getLastName());
                            oos.flush();
                            oos.writeDouble(currentUser.getBalance());
                            oos.flush();

                            int choice = ois.readInt();

                            switch (choice) {
                                case 1: //Buy tickets
                                    handleBuyTickets(ois, oos, currentUser);
                                    break;
                                case 2: //Return tickets
                                    handleReturnTickets(ois, oos, currentUser);
                                    break;
                                case 3: //See bought tickets
                                    ArrayList<Ticket> tickets = currentUser.getTickets();
                                    StringBuilder sb = new StringBuilder();
                                    if (tickets.isEmpty()) sb.append("No tickets owned.");
                                    else for (Ticket t : tickets) sb.append(t.toString()).append("\n");
                                    oos.writeObject(sb.toString());
                                    oos.flush();
                                    break;
                                case 4: //See upcoming events
                                    oos.writeObject(getEventsString());
                                    oos.flush();
                                    break;
                                case 5: //Add funds
                                    double amount = ois.readDouble();
                                    currentUser.setBalance(currentUser.getBalance() + amount);
                                    USER_DATABASE.updateUser(currentUser);
                                    break;
                                case 6: //Account settings
                                    handleAccountSettings(ois, oos, currentUser);
                                    if (USER_DATABASE.getUserByUsername(currentUser.getUsername()) == null)
                                        loggedIn = false;
                                    break;
                                case 7: //Log out
                                    loggedIn = false;
                                    break;
                            }
                        } catch (Exception e) {
                            loggedIn = false;
                            clientConnected = false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException e) {
            }
        }
    }

    private String getEventsString() {
        ArrayList<SportingEvent> events = SPORTING_EVENT_DATABASE.getAllEvents();
        StringBuilder eventsStr = new StringBuilder();
        for (SportingEvent e : events) {
            eventsStr.append(String.format("ID: %d, Event: %s, Date: %s, Base: $%.2f, Pref: $%.2f\n",
                    e.getId(), e.getEventName(), e.getDate().getTime().toString(), e.getBasePrice(), e.getPreferredPrice()));
        }
        return eventsStr.toString();
    }

    private void handleLockout(ObjectInputStream ois, ObjectOutputStream oos) throws IOException, ClassNotFoundException {
        oos.writeObject(getEventsString());
        oos.flush();

        int eventId = ois.readInt();
        SportingEvent event = SPORTING_EVENT_DATABASE.getEventID(eventId);

        if (event != null) {
            oos.writeBoolean(true);
            oos.flush();

            char startRow = (Character) ois.readObject();
            int startCol = ois.readInt();
            char endRow = (Character) ois.readObject();
            int endCol = ois.readInt();
            String type = (String) ois.readObject();

            for (char r = startRow; r <= endRow; r++) {
                for (int c = startCol; c <= endCol; c++) {
                    String seatNum = r + "" + c;
                    event.buyPreferredTicket(seatNum, type);
                }
            }
            SPORTING_EVENT_DATABASE.updateEvent(event);
        } else {
            oos.writeBoolean(false);
            oos.flush();
        }
    }

    private void handleSetPrices(ObjectInputStream ois, ObjectOutputStream oos) throws IOException {
        oos.writeObject(getEventsString());
        oos.flush();

        int id = ois.readInt();
        SportingEvent event = SPORTING_EVENT_DATABASE.getEventID(id);

        if (event != null) {
            oos.writeBoolean(true);
            oos.flush();
            double base = ois.readDouble();
            double pref = ois.readDouble();

            event.setBasePrice(base);
            event.setPreferredPrice(pref);
            SPORTING_EVENT_DATABASE.updateEvent(event);

        } else {
            oos.writeBoolean(false);
            oos.flush();
        }
    }

    private void handleCreateEvent(ObjectInputStream ois, ObjectOutputStream oos) throws IOException, ClassNotFoundException {
        String name = (String) ois.readObject();
        String home = (String) ois.readObject();
        String away = (String) ois.readObject();
        int y = ois.readInt();
        int m = ois.readInt();
        int d = ois.readInt();
        int h = ois.readInt();
        int min = ois.readInt();
        double base = ois.readDouble();
        double pref = ois.readDouble();

        GregorianCalendar date = new GregorianCalendar(y, m - 1, d, h, min);
        SportingEvent newEvent = new SportingEvent(name, home, away, date, base, pref);
        SPORTING_EVENT_DATABASE.addEvent(newEvent);

        oos.writeObject("Event Created: " + name);
        oos.flush();
    }

    private void handleAccountSettings(ObjectInputStream ois, ObjectOutputStream oos, User currentUser) throws IOException, ClassNotFoundException {
        int option = ois.readInt();
        if (option == 1) {
            String n1 = (String) ois.readObject();
            String n2 = (String) ois.readObject();
            currentUser.setFirstName(n1);
            currentUser.setLastName(n2);
            USER_DATABASE.updateUser(currentUser);
            oos.writeObject(currentUser.getFirstName());
            oos.writeObject(currentUser.getLastName());
            oos.writeDouble(currentUser.getBalance());
            oos.flush();
        } else if (option == 2) {
            String p1 = (String) ois.readObject();
            String p2 = (String) ois.readObject();
            if (p1.equals(p2)) {
                currentUser.setPassword(p1);
                USER_DATABASE.updateUser(currentUser);
            }
            oos.writeObject(currentUser.getFirstName());
            oos.writeObject(currentUser.getLastName());
            oos.writeDouble(currentUser.getBalance());
            oos.flush();
        } else if (option == 3) {
            boolean del = ois.readBoolean();
            if (del) USER_DATABASE.removeUser(currentUser.getUsername());
        }
    }

    /**
     * Handles the ticket purhasing processs including presenting events,
     * seat selection, ticket assignment, balance update, and persisting changes.
     *
     * @param ois
     * @param oos
     * @param user
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void handleBuyTickets(ObjectInputStream ois, ObjectOutputStream oos, User user) throws IOException, ClassNotFoundException {
        //Send list of events to client
        oos.writeObject(getEventsString());
        oos.flush();

        int eventId = ois.readInt();
        SportingEvent selectedEvent = SPORTING_EVENT_DATABASE.getEventID(eventId);

        if (selectedEvent == null) {
            oos.writeBoolean(false);
            oos.flush();
            return;
        } else {
            oos.writeBoolean(true);
            oos.flush();
        }

        GregorianCalendar today = new GregorianCalendar();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        double diff = selectedEvent.getDate().getTimeInMillis() - today.getTimeInMillis();
        double days = diff / (1000 * 60 * 60 * 24);

        if (days > 7) {
            oos.writeBoolean(false);
            oos.writeObject("Too far in future");
            oos.flush();
            return;
        } else if (days < 0) {
            oos.writeBoolean(false);
            oos.writeObject("Event passed");
            oos.flush();
            return;
        } else {
            oos.writeBoolean(true);
            oos.flush();
        }

        int seatChoice = ois.readInt(); //1 = Home, 2 = Away
        int method = ois.readInt(); //1 = first available, 2 = preferred seat
        int count = ois.readInt();

        double price = (method == 1) ? count * selectedEvent.getBasePrice() : count * selectedEvent.getPreferredPrice();

        boolean fundsOK = price <= user.getBalance();
        boolean ticketsOK = (seatChoice == 1) ? count <= selectedEvent.getHomeTicketsRemaining() : count <= selectedEvent.getAwayTicketsRemaining();

        if (fundsOK && ticketsOK) {
            oos.writeBoolean(true);
            oos.flush();
        } else {
            oos.writeBoolean(false);
            oos.flush();
            return;
        }

        if (method == 1) {
            // Case 1: First Available
            Ticket[] tix = (seatChoice == 1) ? selectedEvent.buyHomeTickets(count) : selectedEvent.buyAwayTickets(count);
            if (tix == null) {
                oos.writeObject("Not enough tickets");
                oos.flush();
                return;
            }
            StringBuilder sb = new StringBuilder("Seats: ");
            for (Ticket t : tix) sb.append(t.getSeatNumber()).append(" ");
            sb.append(String.format("\nTotal Price: $%.2f", price));
            oos.writeObject(sb.toString());
            oos.flush();

            String conf = (String) ois.readObject();
            if ("y".equals(conf)) {
                // Commit purchase
                for (Ticket t : tix) user.addTicket(t);
                user.setBalance(user.getBalance() - price);
                USER_DATABASE.updateUser(user);
                SPORTING_EVENT_DATABASE.updateEvent(selectedEvent);
            } else {
                // Revert purchase
                for (Ticket t : tix) selectedEvent.returnTicket(t);
            }
        } else {
            // Case 2: Choose Seating
            String grid = selectedEvent.gridString(seatChoice == 1 ? "Home" : "Away");
            oos.writeObject(grid);
            oos.flush();

            ArrayList<Ticket> temp = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                while (true) {
                    String sNum = (String) ois.readObject();
                    if ("INVALID".equals(sNum)) {
                        oos.writeBoolean(false);
                        oos.flush();
                        continue;
                    }
                    Ticket t = selectedEvent.buyPreferredTicket(sNum, seatChoice == 1 ? "Home" : "Away");
                    if (t != null) {
                        temp.add(t);
                        oos.writeBoolean(true);
                        oos.flush();
                        break;
                    } else {
                        oos.writeBoolean(false);
                        oos.flush();
                    }
                }
            }
            // Send tickets summary to client for confirmation
            StringBuilder sum = new StringBuilder("Tickets Summary:\n");
            // Use tempTickets for summary, not user tickets yet
            for (Ticket t : temp) sum.append(t.toString()).append("\n");
            sum.append(String.format("\nTotal Price: $%.2f", price));
            oos.writeObject(sum.toString());
            oos.flush();

            String conf = (String) ois.readObject();
            if ("y".equals(conf)) {
                // Commit
                for (Ticket t : temp) user.addTicket(t);
                user.setBalance(user.getBalance() - price);
                USER_DATABASE.updateUser(user);
                SPORTING_EVENT_DATABASE.updateEvent(selectedEvent);
            } else {
                // Revert
                for (Ticket t : temp) selectedEvent.returnTicket(t);
            }
        }
    }

    /**
     * Handles ticket return process by showing owned tickets,
     * accepting returns, updating user and event data, and confirming
     * success.
     *
     * @param ois
     * @param oos
     * @param user
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void handleReturnTickets(ObjectInputStream ois, ObjectOutputStream oos, User user) throws IOException, ClassNotFoundException {
        ArrayList<Ticket> tickets = user.getTickets();
        StringBuilder owned = new StringBuilder();
        for (Ticket t : tickets) owned.append(t.toString()).append("\n");
        oos.writeObject(owned.toString());
        oos.flush();
        oos.writeInt(tickets.size());
        oos.flush();

        int num = ois.readInt();
        if (num <= 0) return;

        for (int i = 0; i < num; i++) {
            int eid = ois.readInt();
            String seat = (String) ois.readObject();
            Ticket ret = user.removeTicket(eid, seat);

            // Remove ticket from user
            if (ret != null) {
                SportingEvent ev = SPORTING_EVENT_DATABASE.getEventID(eid);
                if (ev != null) {
                    ev.returnTicket(ret);
                    SPORTING_EVENT_DATABASE.updateEvent(ev);
                    USER_DATABASE.updateUser(user);
                }
            }
            StringBuilder up = new StringBuilder();
            for (Ticket t : user.getTickets()) up.append(t.toString()).append("\n");
            oos.writeObject(up.toString());
            oos.flush();
        }
    }

    /**
     * Server main method accepting client connections and starting a
     * handler thread for each.
     *
     * @param args
     */
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8888)) {
            System.out.println("Server started on port 8888");
            GregorianCalendar date = new GregorianCalendar(2025, Calendar.DECEMBER, 14);
            SportingEvent event = new SportingEvent("Purdue vs. IU Basketball", "Purdue", "IU", date, 70.00, 150.00);
            if (SPORTING_EVENT_DATABASE.getEventID(event.getId()) == null) SPORTING_EVENT_DATABASE.addEvent(event);

            while (true) {
                Socket socket = serverSocket.accept();
                new SportingEventServer(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
