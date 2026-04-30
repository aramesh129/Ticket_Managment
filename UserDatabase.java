package sporting_event_ticketmanager.src;

import java.io.*;
import java.util.ArrayList;

/**
 * CS 180 Team Project
 * Database class for users, uses file writing for persistence.
 * Contains many methods useful methods for phase 2 such as user validation and such
 * @author Ashvath Raj Ramesh, 36
 * @version Nov 7th, 2025
 */

public class UserDatabase implements UserDatabaseable {
    private ArrayList<User> users;
    private static final Object LOCK = new Object();

    public UserDatabase() {         //UserDatabase constructor
        users = new ArrayList<>();
        loadDatabase();
    }

    public boolean addUser(User user) { // method to add user to the database
        synchronized (LOCK) {
            if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
                return false;
            }

            if (getUserByUsername(user.getUsername()) != null) {
                return false;
            }
            users.add(user);
            saveUser(user);
            return true;
        }
    }

    public boolean removeUser(String username) { // method to remove user from the database
        synchronized (LOCK) {
            if (username == null || username.isEmpty()) {
                return false;
            }

            User user = getUserByUsername(username);
            if (user != null) {
                users.remove(user);
                File file = new File(username + ".ser");
                if (file.exists()) {
                    file.delete();
                }
                return true;
            }
            return false;
        }
    }

    public User getUserByUsername(String username) { // method to return user when username is given as argument
        synchronized (LOCK) {
            if (username == null || username.isEmpty()) {
                return null;
            }

            for (User u : users) {
                if (u.getUsername().equals(username)) {
                    return u;
                }
            }
            return null;
        }
    }

    public boolean validateUser(String username, String password) { // method to validate login credentials
        synchronized (LOCK) {
            if (username == null || password == null || username.isEmpty()
                    || password.isEmpty()) {
                return false;
            }

            User user = getUserByUsername(username);
            return user != null && user.getPassword().equals(password);
        }
    }

    public boolean updateUser(User updatedUser) {
        synchronized (LOCK) {
            if (updatedUser == null || updatedUser.getUsername() == null
                    || updatedUser.getUsername().isEmpty()) {
                return false;
            }

            for (int i = 0; i < users.size(); i++) {
                User existingUser = users.get(i);
                if (existingUser.getId() == updatedUser.getId()) {

                    // If the username has changed, delete the old file
                    if (!existingUser.getUsername().equals(updatedUser.getUsername())) {
                        File oldFile = new File(existingUser.getUsername() + ".ser");
                        if (oldFile.exists()) {
                            oldFile.delete();
                        }
                    }

                    users.set(i, updatedUser);
                    saveUser(updatedUser);
                    return true;
                }
            }
            return false;
        }
    }


    public ArrayList<User> getAllUsers() { // returns all existing users
        synchronized (LOCK) {
            return new ArrayList<>(users);
        }
    }

    public int getUserCount() { // return total user count
        synchronized (LOCK) {
            return users.size();
        }
    }

    public void printAllUsers() { // method to print out all existing users
        synchronized (LOCK) {
            for (User u : users) {
                System.out.printf("%s | %s %s | Balance: $%.2f%n",
                        u.getUsername(), u.getFirstName(),
                        u.getLastName(), u.getBalance());
            }
        }
    }

    private void saveUser(User user) { // helper method that creates a file for each user
        synchronized (LOCK) {
            if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
                System.out.println("Error: Cannot save null or invalid user.");
                return;
            }

            File file = new File(user.getUsername() + ".ser");
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(user);
            } catch (IOException e) {
                System.out.println("Error saving user " + user.getUsername()
                        + ": " + e.getMessage());
            }
        }
    }

    private void loadDatabase() {        //helper method to read in existing user files to the program 
        //essentially loading up the entire current userdatabase
        synchronized (LOCK) {
            File[] allFiles = new File(".").listFiles();
            int maxId = 0; // Track the highest ID loaded
            if (allFiles != null) {
                for (File f : allFiles) {
                    if (f.isFile() && f.getName().endsWith(".ser")) {
                        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(f))) {
                            Object obj = in.readObject();
                            if (obj instanceof User) {
                                User user = (User) obj;
                                users.add(user);

                                // Update maxId
                                if (user.getId() > maxId) {
                                    maxId = user.getId();
                                }
                            }
                        } catch (IOException e) {
                            System.out.println("IO error loading user from " +
                                    f.getName() + ": " +
                                    e.getMessage());
                        } catch (ClassNotFoundException e) {
                            System.out.println("Class not found loading user from " +
                                    f.getName() + ": "
                                    + e.getMessage());
                        }
                    }
                }
            }
            User.setUserCount(maxId);       //sets the user count to current value so no 
            //possibility of repeat user Ids
        }
    }
}
