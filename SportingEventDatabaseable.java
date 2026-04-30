package sporting_event_ticketmanager.src;

import java.util.List;
import java.util.ArrayList;

/**
 * CS180 Team Project
 * @author Akhil Kasturi, L36 Group 4
 * @version Nov 6, 2025
 * Interface for SportingEventDatabase class
 */
public interface SportingEventDatabaseable {
    /** addEvent
     * Adds new event to database.
     * @param event - SportingEvent object
     */
    boolean addEvent(SportingEvent event);

    /** removeEvent
     * Removes event to database.
     * @param eventID - Object ID of removing event
     */

    boolean removeEvent(int eventID);

    /** getEventID
     * Gets specified event from database.
     * @param eventID - Object ID of retrieved event.
     */

    SportingEvent getEventID(int eventID);

    /** updateEvent
     * Updates existing event in database.
     * @param event - SportingEvent object
     */


    boolean updateEvent(SportingEvent event);

    /** getAllEvents
     * Gets a list of sporting events in database.
     */

    ArrayList<SportingEvent> getAllEvents();

    /** getEventCount
     * Gets total count of events from database.
     */

    int getEventCount();

    /** printAllEvents
     * Prints summary of events to console.
     */

    void printAllEvents();



}
