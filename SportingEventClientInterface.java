package sporting_event_ticketmanager.src;

import java.util.Scanner;
import java.io.*;

/**
 * CS 180 Team Project
 * Interface for the SportingEventClient
 * @author Ashvath Raj Ramesh, 36
 * @version Nov 23, 2025
 */

public interface SportingEventClientInterface {
    void login(Scanner sc, ObjectInputStream ois, ObjectOutputStream oos) 
        throws IOException;   //login functionality for an existing user
    void buyTicket(Scanner sc, ObjectInputStream ois, ObjectOutputStream oos) 
        throws IOException, ClassNotFoundException;   //allows users to buy tickets for the sporting event
    void returnTicket(Scanner sc, ObjectInputStream ois, ObjectOutputStream oos) 
        throws IOException, ClassNotFoundException;    //allows users to return previously purchased tickets
    void addFunds(Scanner sc, ObjectOutputStream oos) 
        throws IOException;   //adds funds to the users account
    void accountSettings(Scanner sc, ObjectInputStream ois, ObjectOutputStream oos)  
        throws IOException, ClassNotFoundException;    //basic settings to change for example username or password
}
