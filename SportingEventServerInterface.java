package sporting_event_ticketmanager.src;

import java.io.*;

/**
 * CS 180 Team Project
 * Interface for the SportingEventServer
 * @version Nov 23, 2025
 */

public interface SportingEventServerInterface {
    void handleBuyTickets(ObjectInputStream ois, ObjectOutputStream oos, User user) 
        throws IOException, ClassNotFoundException; // Handles buying tickets for a user
    void handleReturnTickets(ObjectInputStream ois, ObjectOutputStream oos, User user) 
        throws IOException, ClassNotFoundException; // Handles returning tickets for a user
}
