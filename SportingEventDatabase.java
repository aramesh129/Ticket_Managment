package sporting_event_ticketmanager.src;

import java.io.*;
import java.util.ArrayList;

/**
 * CS180 Team Project
 * Database class for SportingEvents
 * @version Nov 7, 2025
 */

public class SportingEventDatabase implements SportingEventDatabaseable {
    private ArrayList<SportingEvent> events;
    private static final Object LOCK = new Object();

    /**
     * SportingEventDatabase constructor
     * Initializes relevant fields for list
     */
    public SportingEventDatabase() {
        events = new ArrayList<>();
        loadDatabase();
    }

    /**
     * addEvent
     * adds Event to database.
     */
    public boolean addEvent(SportingEvent event) {
        synchronized (LOCK) {
            if (event == null) {
                return false;
            }
            if (getEventID(event.getId()) != null) {
                return false;
            }
            events.add(event);
            saveEvent(event);
            return true;
        }
    }

    /**
     * removeEvent
     * Removes event from database.
     */
    public boolean removeEvent(int eventID) {
        synchronized (LOCK) {
            SportingEvent event = getEventID(eventID);
            if (event != null) {
                events.remove(event);

                File file = new File(event.getId() + ".dat");
                if (file.exists()) {
                    file.delete();
                }
                return true;
            }
            return false;
        }
    }

    /**
     * getEventID
     * Gets specified event from database.
     */
    public SportingEvent getEventID(int eventID) {
        synchronized (LOCK) {
            for (SportingEvent e : events) {
                if (e.getId() == eventID) {
                    return e;
                }
            }
            return null;
        }
    }

    /**
     * updateEvent
     * Updates existing event in database.
     */
    public boolean updateEvent(SportingEvent event) {
        synchronized (LOCK) {
            if (event == null) {
                return false;
            }
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getId() == event.getId()) {
                    events.set(i, event);
                    saveEvent(event);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * getAllEvents
     * Gets ist of sporting events in database.
     */
    public ArrayList<SportingEvent> getAllEvents() {
        synchronized (LOCK) {
            return new ArrayList<>(events);
        }
    }

    /**
     * getEventCount
     * Gets count of events from database.
     */
    public int getEventCount() {
        synchronized (LOCK) {
            return events.size();
        }
    }

    /**
     * printAllEvents
     * Prints summary of events.
     */
    public void printAllEvents() {
        synchronized (LOCK) {
            if (events.isEmpty()) {
                System.out.println("No events in database.");
                return;
            }
            for (SportingEvent e : events) {
                System.out.printf("ID : %d | %s (%s vs %s) | Date: %s | " +
                                "Home Tickets Left : %d | Away Tickets Left : %d\n",
                        e.getId(), e.getEventName(), e.getAwayTeam(),
                        e.getHomeTeam(), e.getDate().getTime().toString(),
                        e.getHomeTicketsRemaining(),  e.getAwayTicketsRemaining());
            }
        }
    }

    /**
     * saveEvent
     * creates a file for each event.
     */
    private void saveEvent(SportingEvent event) {
        synchronized (LOCK) {
            if (event == null) {
                System.out.println("Error : Null Event");
                return;
            }
            File file = new File(event.getId() + ".dat");
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(event);
            } catch (IOException e) {
                System.out.println("Error with saving event " + event.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * loadDatabase
     * reads existing event files to the program.
     */
    private void loadDatabase() {
        synchronized (LOCK) {
            File[] allFiles = new File(".").listFiles();
            int max = -1;
            if (allFiles != null) {
                for (File f : allFiles) {
                    if (f.isFile() && f.getName().endsWith(".dat")) {
                        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
                            Object o = in.readObject();
                            if (o instanceof SportingEvent) {
                                SportingEvent event = (SportingEvent) o;
                                events.add(event);

                                if (event.getId() > max) {
                                    max = event.getId();
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("IO error loading event from " + f.getName() + ": " + e.getMessage());

                        } catch (ClassNotFoundException e) {
                            System.out.println("Class Existence error loading event from" + f.getName()
                                    + ": " + e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
