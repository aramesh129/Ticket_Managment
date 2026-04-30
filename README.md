#  Sporting Event Ticket Buyer

## How To Run This Project

`Main Project`
1. Open the project in IntelliJ
2. Compile all of the code
3. Click the green run button in the `SportingEventServer` to start running the server (Make sure to run Server first before Client)
4. Click the green run button in the `GUIApp` to start running the client
5. Interact with the program through the console as I currently implemented console I/O

## Classes and Their Tests

### `Ticket`
Implements both the `Ticketable`(methods concerning tickets) and the `Datable`(methods concerning date and time) interface. This class represents a ticket to a sporting event, it contains data concerning the event's ID, the availability of a seat, the unique identifier for the seat, the type of seat ("Home" or "Away"), the date of the event, and the pricing levels based on whether the `User` chooses the seat they want or not. The `Ticket` is the foundational unit used for the seating in `SportingEvent` and is stored in an array in the User class representing the tickets that the `User` bought. The functionality of this class's methods is limited to accessing and mutating its own private fields.

TicketTest.java validates ticket construction (with standard and default pricing), all getter/setter methods, and string representation formatting.


### `User`
Implements the `Userable` interface(methods concerning the `User` class). This class represents an individual user of the app, it contains data on the current total amount of users, the user’s unique, the user’s unique username, the user’s password, the user’s first name, the user’s last name, the user’s balance, the tickets that the user has bought, and the total amount of tickets the user has bought. In addition to the accessor and mutator methods of this class, this class has methods for adding and removing Ticket objects from a user’s account along with some basic transaction logic, along with a method to sort the user’s tickets by date (from earliest to latest).

UserTest.java covers all user operations: creation, getter/setter accuracy, ticket purchasing and balance management, removing tickets and checking refunds, as well as sorting tickets by date and static user count handling.


### `SportingEvent`
Implements both the `Eventable`(methods concerning sporting events) and the `Datable`(methods concerning date and time) interface. This class represents a sport with a static seating arrangement, two ten by twenty grids each containing two hundred available seats each(each represented by a `Ticket` object), one of the grids is the seating for people supporting the “Home” team, and the other is the seating for people supporting the “Away” team. Other than the seating, this class also contains information about the name of the sporting event, the names of the two competing teams, the date of the event, the pricing levels based on whether the User chooses the seat they want or not, the event’s unique ID, and the total number of sporting events. This class has multiple methods for “buying” tickets whether the user wants a specific seat or not, a method to “return” a ticket, a method to block out a section of either of the seat grids, and a method to determine whether an event occurs in the next week or not.

SportingEventTest.java tests sporting event creation, buying home and away tickets (valid and invalid party sizes), preferred seat purchasing, blocking out seat sections, and date comparisons for upcoming events.


### `SportingEventDatabase`
The `SportingEventDatabase` class implements the `SportingEventDatabaseable` interface, and essentially functions as the structure to handle `SportingEvent` objects and instances in the program. It harbors and updates data for the SportingEvents, and maintains a list to store the information. The class handles essential event methods such as adding and removing events, saving events, updating events, and being able to search for an event given a form of event identification. Additionally, it includes methods to return the list of events as well. The event information is read and written from `.dat` files. The `SportingEventDatabase` class operates as a thread-safe function that handles the creation, editing, saving, and other aspects of the event management handling. The JUnit tests that were implemented tested the functionality by making new instances of `SportingEventDatabase` and testing the capabilities of the functions to add, remove, and update the objects, and checking them to have the expected outputs. 

SportingEventDatabaseTest.java validates core CRUD operations on the event database: adding, removing, retrieving, and updating events including duplication and null handling, along with verifying event count and list retrieval.


### `UserDatabase`
The `UserDatabase` class implements the `UserDatabaseable` interface and acts as the main system responsible for managing all `User` data/instances within the application. It stores, retrieves, and updates user information such as login credentials, balance and purchased `Ticket`s using file I/O, ensuring persistence in user data. This class provides core functionality such as adding new users, validating login credentials, updating existing accounts, and deleting users. All user information is written to and read from `.ser` files. JUnit tests tested all the method within the class by creating user instances and runnning the methods and seeing if they had correct behavior. Overall, `UserDatabase` serves as the backbone of user management. 

### `SportingEventServer`
The `SportingEventServer` class manages all networking and server-side logic for the project. It listens for client connections, starts a new thread for each client, and processes commands such as login, signup, ticket transactions, and account updates. All computation and data management occur on the server, with communication handled through Java object streams. The class ensures thread safety and persistence by implementing synchronization as well as interacting with the shared `UserDatabase` and `SportingEventDatabase`, making it possible for multiple users to access and modify data concurrently without conflicts. The class also includes functionality to handle user settings, and ensures proper handling of all input and output streams. This class is central to coordinating client requests and maintaining reliable, secure interactions in the system.

`SportingEventServerTest` tests all of the helper methods within the class such as the handleBuyTickets method, the handleReturnTickets method, and the handleAccountSettings method. They all check for expected output.

### `SportingEventClient`

In this phase, in order to test the functionality of the server that we implemented, we have also created a terminal based client that tests these capabilities:
1. login - ability to log into an account on the server, takes in a username and password, and is able to tell the user if the account doesn’t exist or their password is wrong. Ability to create a new account using signup
2. buyTicket - ability to use the client to either buy the first available tickets, or choose which open seats you would like to buy tickets for.
3. returnTicket - ability to use a client to return a ticket that the customer has bought previously.
4. addFunds - ability to use a client to add funds to your personal account
5. accountSettings - ability to use a client to change your first or last name, change your password, or delete your account.
6. Other tested actions: Show users bought tickets, show upcoming events, log out.

### `GUIApp.java`

The `GUIApp` class is the client facing graphical user interface for the application. It establishes a socket connection to the server and manages the object input and output streams to support real time communication between the two. The class uses many JFrame screens for user authentication, role based menus (Admin vs User), and specific tasks such as ticket purchasing, event management, and account settings. The `GUIApp` is repsonsible for capturing user input and maintaining the navigation flow so the server can respond accordingly without crashing. This class essentially acts as the bridge between the user and the backend logic. 
