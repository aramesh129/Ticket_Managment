package sporting_event_ticketmanager.src;

import java.util.ArrayList;

/**
 * CS 180 Team Project
 * Interface for Userdatabase.java
 * @author Ashvath Raj Ramesh, 36
 * @version Nov 7th, 2025
 */

public interface UserDatabaseable {     //interface for the 
                                        //UserDatabase class with all the methods 
                                        //that are being implemented
  
    boolean addUser(User user);     //method to add user to database

    boolean removeUser(String username);    //method to remove user from database

    User getUserByUsername(String username);    //method to return an instance of User using the username field

    boolean validateUser(String username, String password);  //method to validate user based on 
                                                             //arguments will be used 
                                                             //for login purposes in later implementation

    boolean updateUser(User user);   //method to update User information in database

    ArrayList<User> getAllUsers();   //returns all Users in database

    int getUserCount();         //returns total count of existing users

    void printAllUsers();       // method to print all User instances to console if needed
}
