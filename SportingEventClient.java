package sporting_event_ticketmanager.src;

import java.io.*;
import java.net.*;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Sporting Event Test Client
 *
 * A simple command line client meant to test the
 * capabilities of the Sporting Event server.
 *
 *
 * @version November 22nd, 2025
 *
 */
public class SportingEventClient implements SportingEventClientInterface {
    private static String firstName; // First name from the server
    private static String lastName; // Last name from the server
    private static double balance; // Balance from the server

    /**
     * A method that takes login input from the user and sends it to
     * the server.
     *
     * @param sc A scanner object from the main method
     * @param ois An ObjectInputStream from the socket
     * @param oos An ObjectOutputStream from the socket
     * @throws IOException Thrown when a read or write method fails
     */
    public void login(Scanner sc, ObjectInputStream ois,
                      ObjectOutputStream oos) throws IOException {
        // Show sign in options
        System.out.println("What would you like to do? (Input the number of the option).");
        System.out.println("1. Log in");
        System.out.println("2. Sign up");
        System.out.println("3. Exit");

        // Loop until user selects a valid option
        while (true) {
            boolean correctOptionSelected = false;
            int option;

            // Every time a block in this format appears it is
            // a safe call to scanner to take an int as input
            // loops until the user enters an integer. Assume
            // user is an idiot.
            while (true) {
                try {
                    option = sc.nextInt();
                    sc.nextLine();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("A option is an integer. Try again.");
                    sc.nextLine();
                }
            }

            // Switch case for the options
            switch (option) {
                // Case for login, takes the user's username and password and
                // sends it to the server, if the account doesn't exist ask to
                // repeat and then either try again or end the program.
                case 1:
                    oos.writeObject("login");
                    oos.flush();
                    while (true) {
                        System.out.println("Enter your username:");
                        String username = sc.nextLine();

                        oos.writeObject(username);
                        oos.flush();

                        System.out.println("Enter your password:");
                        String password = sc.nextLine();

                        oos.writeObject(password);
                        oos.flush();

                        if (ois.readBoolean()) {
                            System.out.println("Welcome " + username);
                            break;
                        } else {
                            System.out.println("Invalid username or password.");
                            System.out.println("Would you like to enter another username or password?(y or n)");
                            String answer = sc.nextLine();
                            if (answer.equals("n")) {
                                System.exit(0);
                            } else if (!answer.equals("y")) {
                                System.out.println("Invalid option selected. Ending the program.");
                                System.exit(0);
                            }
                        }
                    }
                    correctOptionSelected = true;
                    break;
                // Case for creating a new account, takes in user details and ends if
                // username is already taken try again
                case 2:
                    oos.writeObject("signup");
                    oos.flush();

                    System.out.println("Enter your first name:");
                    String firstName = sc.nextLine();
                    oos.writeObject(firstName);
                    oos.flush();

                    System.out.println("Enter your last name:");
                    String lastName = sc.nextLine();
                    oos.writeObject(lastName);
                    oos.flush();

                    while (true) {
                        System.out.println("Enter a username:");
                        String username = sc.nextLine();

                        oos.writeObject(username);
                        oos.flush();

                        boolean isTaken = ois.readBoolean();

                        if (isTaken) {
                            System.out.println("This username is already taken. Please try again.");
                        } else {
                            break;
                        }
                    }


                    System.out.println("Enter a password:");
                    String password = sc.nextLine();

                    oos.writeObject(password);
                    oos.flush();
                    correctOptionSelected = true;
                    break;
                // Exit case
                case 3:
                    System.exit(0);
                default:
                    System.out.println("Invalid option selected.");
            }

            // Breaks out of the loop if a valid option was selected
            if (correctOptionSelected) {
                break;
            }
        }
    }

    /**
     * Prompts the user so they can buy a ticket from an event
     *
     * @param sc A scanner object from the main method
     * @param ois An ObjectInputStream from the socket
     * @param oos An ObjectOutputStream from the socket
     * @throws IOException Thrown when a read or write method fails
     * @throws ClassNotFoundException Thrown when an unknown class is sent from the server
     */
    public void buyTicket(Scanner sc, ObjectInputStream ois,
                          ObjectOutputStream oos) throws IOException, ClassNotFoundException {
        String upcomingEvents = (String) ois.readObject();
        System.out.println("Upcoming Sporting Events:\n" + upcomingEvents);

        System.out.println("Please enter the event ID of the event " +
                "you would like to buy tickets for");

        // User selects an event
        int eventID;

        while (true) {
            try {
                eventID = sc.nextInt();
                sc.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("A event ID is an integer. Try again.");
                sc.nextLine();
            }
        }

        oos.writeInt(eventID);
        oos.flush();

        if (!ois.readBoolean()) {
            System.out.println("Event ID doesn't correspond to an upcoming Sporting Event.");
            return;
        }

        boolean isDateValid = ois.readBoolean();
        if (!isDateValid) {
            String dateError = (String) ois.readObject();
            System.out.println(dateError);
            return; // Return immediately so we don't send extra data to the server
        }

        // User selects the seating they want
        System.out.println("What type of seating would you prefer?");
        System.out.println("1. Home");
        System.out.println("2. Away");

        int seatingChoice;

        while (true) {
            try {
                seatingChoice = sc.nextInt();
                sc.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("A seating type is an integer. Try again.");
                sc.nextLine();
            }
        }

        oos.writeInt(seatingChoice);
        oos.flush();

        if (seatingChoice != 1 && seatingChoice != 2) {
            System.out.println("Invalid option selected.");
            return;
        }

        // User selects seating selection method
        System.out.println("Select an option:");
        System.out.println("1. Get First Available Seating");
        System.out.println("2. Choose Seating");

        int selection;

        while (true) {
            try {
                selection = sc.nextInt();
                sc.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("A selection is an integer. Try again.");
                sc.nextLine();
            }
        }

        oos.writeInt(selection);
        oos.flush();

        if (selection != 1 && selection != 2) {
            System.out.println("Invalid option selected.");
            return;
        }

        // Take user input for how many tickets they would like
        System.out.println("How many tickets would you like?");

        int tickets;
        while (true) {
            try {
                tickets = sc.nextInt();
                sc.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("A number of tickets is an integer. Try again.");
                sc.nextLine();
            }
        }

        oos.writeInt(tickets);
        oos.flush();

        if (!ois.readBoolean()) {
            System.out.println("Either funds are insufficient or there are not enough tickets.");
            return;
        }

        // Switch for the selection methods
        switch (selection) {
            // Grabs the first available and asks the user if they would like
            // to buy it.
            case 1:
                String foundTickets = (String) ois.readObject();
                System.out.println(foundTickets);
                System.out.println("Are these the tickets you would like to buy? (y or n)");

                String answer;

                while (true) {
                    answer = sc.nextLine();
                    if (answer.equals("y")) {
                        oos.writeObject(answer);
                        oos.flush();
                        break;
                    } else if (answer.equals("n")) {
                        oos.writeObject(answer);
                        oos.flush();
                        break;
                    } else  {
                        System.out.println("Invalid option selected. Try again.");
                    }
                }
                break;
            // Case for the user selecting the tickets they want, loops the amount of times
            // needed for each ticket, asks user again if that seat is not available anymore.
            // At the end, displays info on tickets and confirms if the user wants to buy them.
            case 2:
                // I will write a sporting event method for this called getGrid();
                // This will make this part slightly easier to write
                String seatingGrid = (String) ois.readObject();
                System.out.println(seatingGrid);
                System.out.println("This is the seating for this section.");
                System.out.println("Os represent open seats while Xs represent taken seats.");

                for (int i = 0; i < tickets; i++) {
                    System.out.println("Enter the seat number of ticket #" + (i + 1));

                    while (true) {
                        String seatNumber = sc.nextLine();
                        oos.writeObject(seatNumber);
                        oos.flush();

                        if (ois.readBoolean()) {
                            break;
                        } else {
                            System.out.println("Specified seat was not available. Try again.");
                        }
                    }
                }

                String chosenTickets = (String) ois.readObject();
                System.out.println(chosenTickets);
                System.out.println("Are these the tickets you would like to buy? (y or n)");

                String response;

                while (true) {
                    response = sc.nextLine();
                    if (response.equals("y")) {
                        oos.writeObject(response);
                        oos.flush();
                        break;
                    } else if (response.equals("n")) {
                        oos.writeObject(response);
                        oos.flush();
                        break;
                    } else  {
                        System.out.println("Invalid option selected. Try again.");
                    }
                }
        }
    }

    /**
     * Takes user input to return tickets
     *
     * @param sc A scanner object from the main method
     * @param ois An ObjectInputStream from the socket
     * @param oos An ObjectOutputStream from the socket
     * @throws IOException Thrown when a read or write method fails
     * @throws ClassNotFoundException Thrown when an unknown class is sent from the server
     */
    public void returnTicket(Scanner sc, ObjectInputStream ois,
                             ObjectOutputStream oos) throws IOException, ClassNotFoundException {
        // Show currently owned tickets
        String tickets = (String) ois.readObject();
        int numOfTickets = ois.readInt();
        System.out.println("Currently Owned Tickets:\n" + tickets);

        // Get amount of tickets to return
        System.out.println("How many tickets would you like to return?");
        int numOfReturnTickets;

        while (true) {
            try {
                numOfReturnTickets = sc.nextInt();
                sc.nextLine();

                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter an integer.");
                sc.nextLine();
            }
        }

        oos.writeInt(numOfReturnTickets);
        oos.flush();

        // Check if returned tickets exceeds tickets owned
        if (numOfReturnTickets <= numOfTickets) {
            for (int i = 0; i < numOfReturnTickets; i++) {
                System.out.printf("Select Return Ticket #%d\n", i + 1);

                System.out.println("Please enter the event ID of the ticket you would like to return");
                int eventID = sc.nextInt();
                sc.nextLine();

                oos.writeInt(eventID);
                oos.flush();

                System.out.println("Please enter the seat number of the ticket you would like to return");
                String seatNumber = sc.nextLine();

                oos.writeObject(seatNumber);
                oos.flush();

                tickets = (String) ois.readObject();
                System.out.println(tickets);
            }
            System.out.println("All tickets have been returned and refunded.");
        } else {
            System.out.println("Invalid number of tickets.");
        }
    }

    /**
     * Takes user input to add funds to her account by taking in
     * a double and then adding that number to both the local
     * balance and the User balance on the server.
     *
     * @param sc A scanner object from the main method
     * @param oos An ObjectOutputStream from the socket
     * @throws IOException Thrown when a read or write method fails
     */
    public void addFunds(Scanner sc, ObjectOutputStream oos)  throws IOException {
        System.out.println("How much money would you like to add?");
        double amount;

        while (true) {
            try {
                amount = sc.nextDouble();
                sc.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter a decimal number rounded to the hundredths place.");
                sc.nextLine();
            }
        }

        oos.writeDouble(amount);
        oos.flush();
        System.out.println(String.format("You added %.2f to your account!", amount));
        balance += amount;
    }

    /**
     * Some basic user settings to change the user's name
     * password, and to delete their account.
     *
     * @param sc A scanner object from the main method
     * @param ois An ObjectInputStream from the socket
     * @param oos An ObjectOutputStream from the socket
     * @throws IOException Thrown when a read or write method fails
     * @throws ClassNotFoundException Thrown when an unknown class is sent from the server
     */
    public void accountSettings(Scanner sc, ObjectInputStream ois,
                                ObjectOutputStream oos)  throws IOException, ClassNotFoundException {
        System.out.println("1. Change Name");
        System.out.println("2. Change Password");
        System.out.println("3. Delete Account");

        // Take in user's selection
        int selection;

        while (true) {
            try {
                selection = sc.nextInt();
                sc.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please enter an integer.");
                sc.nextLine();
            }
        }
        oos.writeInt(selection);
        oos.flush();

        if (selection == 1) {
            // Takes in names to change the User's names, and
            // sends them to the server
            System.out.println("Please enter a first name:");
            String nameOne = sc.nextLine();

            oos.writeObject(nameOne);
            oos.flush();
            firstName = nameOne;

            System.out.println("Please enter a last name:");
            String nameTwo = sc.nextLine();

            oos.writeObject(nameTwo);
            oos.flush();
            lastName = nameTwo;

            System.out.printf("Your name has been changed to: %s %s\n", firstName, lastName);
        } else if (selection == 2) {
            // Takes in names to change the User's password, and
            // send it to the server
            System.out.println("Please enter a new password:");
            String newPassword = sc.nextLine();

            oos.writeObject(newPassword);
            oos.flush();

            System.out.println("Please rewrite your new password:");
            String confirmation = sc.nextLine();

            oos.writeObject(confirmation);
            oos.flush();

            System.out.println("Your new password has been changed to: " + confirmation);
        } else if (selection == 3) {
            // Takes in prompt to delete account and sends boolean to the
            // server
            System.out.println("Are you sure you would like to delete your account? (y or n)");
            String confirmation = sc.nextLine();

            if (confirmation.equals("y")) {
                oos.writeBoolean(true);
                oos.flush();
            } else if (confirmation.equals("n")) {
                oos.writeBoolean(false);
                oos.flush();
            } else {
                System.out.println("Invalid input.");
                oos.writeBoolean(false);
                oos.flush();
            }
        } else {
            System.out.println("Invalid selection.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SportingEventClient client = new SportingEventClient();

        String hostname = "localhost";
        int port = 8888;

        // Attempt to connect to the server
        try (Socket socket = new Socket(hostname, port)) {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

            client.login(sc, ois, oos);

            while (true) {
                firstName = (String) ois.readObject();
                lastName = (String) ois.readObject();
                balance = ois.readDouble();

                System.out.println(String.format("Name: %s %s\nBalance: %.2f", firstName, lastName, balance));
                System.out.println("Select an operation:");
                System.out.println("1. Buy Tickets");
                System.out.println("2. Return Tickets");
                System.out.println("3. See Bought Tickets");
                System.out.println("4. See Upcoming Events");
                System.out.println("5. Add Funds");
                System.out.println("6. Account Settings");
                System.out.println("7. Log out");

                int choice;

                // Restart loop when choice isn't a integer
                try {
                    choice = sc.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Please enter an integer.");
                    sc.nextLine();
                    continue;
                } finally {
                    sc.nextLine();
                }

                oos.writeInt(choice);
                oos.flush();

                // Switch case for all of the choices in the above message
                switch (choice) {
                    case 1:
                        client.buyTicket(sc, ois, oos);
                        break;
                    case 2:
                        client.returnTicket(sc, ois, oos);
                        break;
                    case 3:
                        String currentlyOwned = (String) ois.readObject();
                        System.out.println("Currently Owned Tickets:\n" + currentlyOwned);
                        break;
                    case 4:
                        String upcomingEvents = (String) ois.readObject();
                        System.out.println("Upcoming Sporting Events:\n" + upcomingEvents);
                        break;
                    case 5:
                        client.addFunds(sc, oos);
                        break;
                    case 6:
                        client.accountSettings(sc, ois, oos);
                        break;
                    case 7:
                        System.out.println("Thank you for using Sporting Event Client!");
                        return;
                    default:
                        System.out.println("Invalid option selected. Please choose from 1 to 7.");
                }
            }

        } catch (IOException e) {
            System.out.println("Could not connect to the server");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
