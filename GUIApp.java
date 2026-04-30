package sporting_event_ticketmanager.src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
 * The full client facing GUI of the application
 * also includes admin menu as well
 *
 * @version 12/06/2025
 */

public class GUIApp {

    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    public GUIApp(ObjectInputStream in, ObjectOutputStream out) {
        this.ois = in;
        this.oos = out;
        new AuthSelector(ois, oos);
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8888); // Connect to server on port 8888
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            new GUIApp(ois, oos); // Start the app
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not connect to server.");
            e.printStackTrace();
        }
    }

    private class AuthSelector extends JFrame { // Screen for Log In/Sign Up
        ObjectInputStream ois;
        ObjectOutputStream oos;

        public AuthSelector(ObjectInputStream ois, ObjectOutputStream oos) {
            this.ois = ois;
            this.oos = oos;
            init();
        }

        public void init() {
            setTitle("Auth Selector");
            setSize(400, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(null);

            JButton logInButton = new JButton("Log In");
            logInButton.setBounds(10, 100, 360, 80);
            logInButton.addActionListener(e -> {
                dispose();
                new LoginGUI(ois, oos);
            });

            JButton signUpButton = new JButton("Sign Up");
            signUpButton.setBounds(10, 300, 360, 80);
            signUpButton.addActionListener(e -> {
                dispose();
                new SignupGUI(ois, oos);
            });

            add(logInButton);
            add(signUpButton);
            setVisible(true);
        }
    }

    private class LoginGUI extends JFrame { // Screen for existing users to log in
        ObjectInputStream ois;
        ObjectOutputStream oos;
        JTextField username;
        JPasswordField password;

        public LoginGUI(ObjectInputStream ois, ObjectOutputStream oos) {
            this.ois = ois;
            this.oos = oos;
            init();
        }

        public void init() {
            setTitle("Login");
            setSize(400, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(null);

            JLabel uLabel = new JLabel("Username:");
            uLabel.setBounds(50, 120, 300, 20);
            username = new JTextField();
            username.setBounds(50, 140, 300, 30);

            JLabel pLabel = new JLabel("Password:");
            pLabel.setBounds(50, 200, 300, 20);
            password = new JPasswordField();
            password.setBounds(50, 220, 300, 30);

            JButton loginBtn = new JButton("Log In");
            loginBtn.setBounds(150, 300, 100, 30);
            loginBtn.addActionListener(e -> attemptLogin());

            add(uLabel);
            add(username);
            add(pLabel);
            add(password);
            add(loginBtn);
            setVisible(true);
        }

        private void attemptLogin() {
            try {
                oos.writeObject("login");
                oos.writeObject(username.getText());
                oos.writeObject(new String(password.getPassword()));
                oos.flush();

                boolean loggedIn = ois.readBoolean();
                if (loggedIn) {
                    boolean isAdmin = ois.readBoolean();
                    dispose();
                    if (isAdmin) {
                        new AdminMenuGUI(ois, oos);
                    } else {
                        new CustomerMenuGUI(ois, oos);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Login failed.");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class SignupGUI extends JFrame {    // Screen to create a new account
        ObjectInputStream ois;
        ObjectOutputStream oos;
        JTextField username, password, firstName, lastName;

        public SignupGUI(ObjectInputStream ois, ObjectOutputStream oos) {
            this.ois = ois;
            this.oos = oos;
            init();
        }

        public void init() {
            setTitle("Sign Up");
            setSize(400, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(10, 1));

            add(new JLabel("Username:"));
            username = new JTextField();
            add(username);

            add(new JLabel("Password:"));
            password = new JTextField();
            add(password);

            add(new JLabel("First Name:"));
            firstName = new JTextField();
            add(firstName);

            add(new JLabel("Last Name:"));
            lastName = new JTextField();
            add(lastName);

            JButton signBtn = new JButton("Create Account");
            signBtn.addActionListener(e -> attemptSignup());
            add(signBtn);

            setVisible(true);
        }

        private void attemptSignup() {
            try {
                oos.writeObject("signup");
                oos.writeObject(firstName.getText());
                oos.writeObject(lastName.getText());
                oos.writeObject(password.getText());
                oos.writeObject(username.getText());
                oos.flush();

                boolean isTaken = ois.readBoolean();
                if (isTaken) {
                    JOptionPane.showMessageDialog(this, "Username taken.");
                } else {
                    boolean isAdmin = ois.readBoolean();
                    dispose();
                    new CustomerMenuGUI(ois, oos);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class AdminMenuGUI extends JFrame { // Dashboard for admin options like creating events
        ObjectInputStream ois;
        ObjectOutputStream oos;

        public AdminMenuGUI(ObjectInputStream ois, ObjectOutputStream oos) {
            this.ois = ois;
            this.oos = oos;
            init();
        }

        private void init() {
            setTitle("ADMINISTRATOR");
            setSize(400, 500);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(4, 1));

            JButton b1 = new JButton("1. Lock Out Section");
            b1.addActionListener(e -> lockSection());
            add(b1);

            JButton b2 = new JButton("2. Set Variable Prices");
            b2.addActionListener(e -> setPrices());
            add(b2);

            JButton b3 = new JButton("3. Create New Event");
            b3.addActionListener(e -> createEvent());
            add(b3);

            JButton b4 = new JButton("4. Logout");
            b4.addActionListener(e -> logout());
            add(b4);

            setVisible(true);
        }

        private void lockSection() {    // Option 1: Block a section of seats
            try {
                oos.writeInt(1);
                oos.flush();
                String events = (String) ois.readObject();
                String idS = JOptionPane.showInputDialog(events + "\nEnter Event ID:");
                if (idS == null) return;

                oos.writeInt(Integer.parseInt(idS));
                oos.flush();

                if (ois.readBoolean()) {
                    String startR = JOptionPane.showInputDialog("Start Row (A-J):");
                    String startC = JOptionPane.showInputDialog("Start Col (0-19):");
                    String endR = JOptionPane.showInputDialog("End Row (A-J):");
                    String endC = JOptionPane.showInputDialog("End Col (0-19):");
                    String type = JOptionPane.showInputDialog("Type (Home/Away):");

                    oos.writeObject(startR.toUpperCase().charAt(0));
                    oos.writeInt(Integer.parseInt(startC));
                    oos.writeObject(endR.toUpperCase().charAt(0));
                    oos.writeInt(Integer.parseInt(endC));
                    oos.writeObject(type);
                    oos.flush();
                    JOptionPane.showMessageDialog(this, "Section Locked.");
                } else {
                    JOptionPane.showMessageDialog(this, "Event not found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void setPrices() {  // Option 2: Change ticket prices
            try {
                oos.writeInt(2);
                oos.flush();
                String events = (String) ois.readObject();
                String idS = JOptionPane.showInputDialog(events + "\nEnter Event ID:");
                if (idS == null) return;

                oos.writeInt(Integer.parseInt(idS));
                oos.flush();

                if (ois.readBoolean()) {
                    String base = JOptionPane.showInputDialog("New Base Price:");
                    String pref = JOptionPane.showInputDialog("New Preferred Price:");
                    oos.writeDouble(Double.parseDouble(base));
                    oos.writeDouble(Double.parseDouble(pref));
                    oos.flush();
                    JOptionPane.showMessageDialog(this, "Prices Updated.");
                } else {
                    JOptionPane.showMessageDialog(this, "Event not found.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void createEvent() {    // Option 3: Make a new game/event
            try {
                oos.writeInt(3);
                oos.flush();
                String name = JOptionPane.showInputDialog("Event Name:");
                String home = JOptionPane.showInputDialog("Home Team:");
                String away = JOptionPane.showInputDialog("Away Team:");
                int y = Integer.parseInt(JOptionPane.showInputDialog("Year (e.g. 2025):"));
                int m = Integer.parseInt(JOptionPane.showInputDialog("Month (1-12):"));
                int d = Integer.parseInt(JOptionPane.showInputDialog("Day (1-31):"));
                int h = Integer.parseInt(JOptionPane.showInputDialog("Hour (0-23):"));
                int min = Integer.parseInt(JOptionPane.showInputDialog("Minute (0-59):"));
                double b = Double.parseDouble(JOptionPane.showInputDialog("Base Price:"));
                double p = Double.parseDouble(JOptionPane.showInputDialog("Preferred Price:"));

                oos.writeObject(name);
                oos.writeObject(home);
                oos.writeObject(away);
                oos.writeInt(y);
                oos.writeInt(m);
                oos.writeInt(d);
                oos.writeInt(h);
                oos.writeInt(min);
                oos.writeDouble(b);
                oos.writeDouble(p);
                oos.flush();

                String conf = (String) ois.readObject();
                JOptionPane.showMessageDialog(this, conf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void logout() { // Option 4: Log out
            try {
                oos.writeInt(4);
                oos.flush();
                dispose();
                new AuthSelector(ois, oos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CustomerMenuGUI extends JFrame {  // Dashboard for regular users
        ObjectInputStream ois;
        ObjectOutputStream oos;

        public CustomerMenuGUI(ObjectInputStream ois, ObjectOutputStream oos) {
            this.ois = ois;
            this.oos = oos;
            init();
        }

        public void init() {
            String fName = "", lName = "";
            double balance = 0.0;
            try {
                fName = (String) ois.readObject();
                lName = (String) ois.readObject();
                balance = ois.readDouble();
            } catch (Exception e) {
                e.printStackTrace();
            }

            setTitle("Welcome " + fName);
            setSize(400, 600);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new GridLayout(8, 1));

            JLabel balLabel = new JLabel("Balance: $" + String.format("%.2f", balance), SwingConstants.CENTER);
            balLabel.setFont(new Font("Arial", Font.BOLD, 20));
            add(balLabel);

            JButton b1 = new JButton("1. Buy Tickets");
            b1.addActionListener(e -> {
                try {
                    oos.writeInt(1);
                    oos.flush();
                    dispose();
                    new BuyTicketGUI(ois, oos);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            add(b1);

            JButton b2 = new JButton("2. Return Tickets");
            b2.addActionListener(e -> {
                try {
                    oos.writeInt(2);
                    oos.flush();
                    String ticketStr = (String) ois.readObject();
                    int size = ois.readInt();
                    dispose();
                    new ReturnTicketsGUI(ois, oos, ticketStr);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            add(b2);

            JButton b3 = new JButton("3. See Bought Tickets");
            b3.addActionListener(e -> {
                try {
                    oos.writeInt(3);
                    oos.flush();
                    String t = (String) ois.readObject();
                    JOptionPane.showMessageDialog(this, t);
                    dispose();
                    new CustomerMenuGUI(ois, oos);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            add(b3);

            JButton b4 = new JButton("4. Upcoming Events");
            b4.addActionListener(e -> {
                try {
                    oos.writeInt(4);
                    oos.flush();
                    String ev = (String) ois.readObject();
                    JOptionPane.showMessageDialog(this, ev);
                    dispose();
                    new CustomerMenuGUI(ois, oos);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
            add(b4);

            JButton b5 = new JButton("5. Add Funds");
            b5.addActionListener(e -> new AddFundsGUI(oos, this));
            add(b5);

            JButton b6 = new JButton("6. Account Settings");
            b6.addActionListener(e -> new UserSettingsGUI(ois, oos));
            add(b6);

            JButton b7 = new JButton("7. Log Out");
            b7.addActionListener(e -> {
                try {
                    oos.writeInt(7);
                    oos.flush();
                    dispose();
                    new AuthSelector(ois, oos);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            add(b7);

            setVisible(true);
        }
    }

    private class BuyTicketGUI extends JFrame { // Screen to purchase tickets
        ObjectInputStream ois;
        ObjectOutputStream oos;
        JTextArea eventsArea;
        JTextField idField, countField;
        JComboBox<String> seatType, methodType;

        public BuyTicketGUI(ObjectInputStream ois, ObjectOutputStream oos) {
            this.ois = ois;
            this.oos = oos;
            init();
        }

        private void init() {
            setTitle("Buy Tickets");
            setSize(500, 600);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            eventsArea = new JTextArea(10, 40);
            eventsArea.setEditable(false);
            try {
                eventsArea.setText((String) ois.readObject());
            } catch (Exception e) {
                e.printStackTrace();
            }
            add(new JScrollPane(eventsArea), BorderLayout.NORTH);

            JPanel form = new JPanel(new GridLayout(6, 2));
            form.add(new JLabel("Event ID:"));
            idField = new JTextField();
            form.add(idField);

            form.add(new JLabel("Seat (Home/Away):"));
            seatType = new JComboBox<>(new String[]{"Home", "Away"});
            form.add(seatType);

            form.add(new JLabel("Method:"));
            methodType = new JComboBox<>(new String[]{"First Available", "Choose Seating"});
            form.add(methodType);

            form.add(new JLabel("Count:"));
            countField = new JTextField();
            form.add(countField);

            JButton buyBtn = new JButton("Purchase");
            buyBtn.addActionListener(e -> purchase());
            form.add(buyBtn);

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.addActionListener(e -> returnToMenu());
            form.add(cancelBtn);

            add(form, BorderLayout.CENTER);
            setVisible(true);
        }

        private void purchase() {
            try {
                int id = Integer.parseInt(idField.getText());
                oos.writeInt(id);
                oos.flush();

                if (!ois.readBoolean()) {
                    JOptionPane.showMessageDialog(this, "Invalid Event ID");
                    returnToMenu();
                    return;
                }
                if (!ois.readBoolean()) {
                    JOptionPane.showMessageDialog(this, (String) ois.readObject());
                    returnToMenu();
                    return;
                }

                oos.writeInt(seatType.getSelectedIndex() + 1);
                oos.writeInt(methodType.getSelectedIndex() + 1);
                int count = Integer.parseInt(countField.getText());
                oos.writeInt(count);
                oos.flush();

                if (!ois.readBoolean()) {
                    JOptionPane.showMessageDialog(this, "Cannot purchase (Funds/Availability).");
                    returnToMenu();
                    return;
                }

                if (methodType.getSelectedIndex() == 0) {
                    String msg = (String) ois.readObject();
                    if (msg.startsWith("Not enough")) {
                        JOptionPane.showMessageDialog(this, msg);
                    } else {
                        int confirm = JOptionPane.showConfirmDialog(this, msg + "\nConfirm?", "Buy", JOptionPane.YES_NO_OPTION);
                        oos.writeObject(confirm == JOptionPane.YES_OPTION ? "y" : "n");
                        oos.flush();
                    }
                } else {
                    String grid = (String) ois.readObject();
                    String[] seats = generateSeats();

                    for (int i = 0; i < count; i++) {
                        while (true) {
                            JPanel p = new JPanel(new BorderLayout());
                            p.add(new JScrollPane(new JTextArea(grid)), BorderLayout.CENTER);
                            JComboBox<String> box = new JComboBox<>(seats);
                            p.add(box, BorderLayout.SOUTH);

                            int res = JOptionPane.showConfirmDialog(this, p, "Select Seat " + (i + 1), JOptionPane.OK_CANCEL_OPTION);
                            if (res != JOptionPane.OK_OPTION) {
                                oos.writeObject("INVALID");
                                oos.flush();
                            } else {
                                oos.writeObject(box.getSelectedItem());
                                oos.flush();
                            }

                            if (ois.readBoolean()) break;
                            else JOptionPane.showMessageDialog(this, "Seat taken/invalid.");
                        }
                    }
                    String summary = (String) ois.readObject();
                    int confirm = JOptionPane.showConfirmDialog(this, summary + "\nConfirm?", "Buy", JOptionPane.YES_NO_OPTION);
                    oos.writeObject(confirm == JOptionPane.YES_OPTION ? "y" : "n");
                    oos.flush();
                }
                JOptionPane.showMessageDialog(this, "Transaction Complete.");
                returnToMenu();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void returnToMenu() {
            dispose();
            new CustomerMenuGUI(ois, oos);
        }

        private String[] generateSeats() {
            ArrayList<String> s = new ArrayList<>();
            for (char r = 'A'; r <= 'J'; r++)
                for (int c = 0; c < 20; c++) s.add(r + "" + c);
            return s.toArray(new String[0]);
        }
    }

    private class ReturnTicketsGUI extends JFrame { // Screen to refund tickets
        ObjectInputStream ois;
        ObjectOutputStream oos;
        JTextArea ticketsArea;
        JTextField eventIdField, seatNumberField;

        public ReturnTicketsGUI(ObjectInputStream ois, ObjectOutputStream oos, String ticketStr) {
            this.ois = ois;
            this.oos = oos;
            init(ticketStr);
        }

        private void init(String ticketStr) {
            setTitle("Return Tickets");
            setSize(500, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    closeAndReturn();
                }
            });
            setLayout(null);

            JLabel info = new JLabel("Your Tickets:");
            info.setBounds(20, 10, 200, 20);
            add(info);

            ticketsArea = new JTextArea(ticketStr);
            ticketsArea.setEditable(false);
            JScrollPane scroll = new JScrollPane(ticketsArea);
            scroll.setBounds(20, 40, 450, 200);
            add(scroll);

            JLabel l1 = new JLabel("Event ID:");
            l1.setBounds(20, 260, 80, 25);
            add(l1);
            eventIdField = new JTextField();
            eventIdField.setBounds(100, 260, 100, 25);
            add(eventIdField);

            JLabel l2 = new JLabel("Seat #:");
            l2.setBounds(220, 260, 80, 25);
            add(l2);
            seatNumberField = new JTextField();
            seatNumberField.setBounds(300, 260, 100, 25);
            add(seatNumberField);

            JButton retBtn = new JButton("Return Ticket");
            retBtn.setBounds(180, 310, 120, 30);
            retBtn.addActionListener(e -> performReturn());
            add(retBtn);

            setVisible(true);
        }

        private void performReturn() {
            try {
                int id = Integer.parseInt(eventIdField.getText());
                String seat = seatNumberField.getText();

                oos.writeInt(1);
                oos.writeInt(id);
                oos.writeObject(seat);
                oos.flush();

                String updated = (String) ois.readObject();
                ticketsArea.setText(updated);
                JOptionPane.showMessageDialog(this, "Ticket Returned.");

                closeAndReturn();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

        private void closeAndReturn() {
            if (eventIdField.getText().isEmpty()) {
                try {
                    oos.writeInt(0);
                    oos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dispose();
            new CustomerMenuGUI(ois, oos);
        }
    }

    private class AddFundsGUI extends JFrame {
        ObjectOutputStream oos;
        JTextField amountField;
        CustomerMenuGUI parentMenu;

        public AddFundsGUI(ObjectOutputStream oos, CustomerMenuGUI parentMenu) { // Screen to add money
            this.oos = oos;
            this.parentMenu = parentMenu;
            init();
        }

        private void init() {
            setTitle("Add Funds");
            setSize(350, 200);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(null);

            JLabel l = new JLabel("Amount:");
            l.setBounds(20, 30, 200, 25);
            add(l);

            amountField = new JTextField();
            amountField.setBounds(20, 60, 200, 30);
            add(amountField);

            JButton btn = new JButton("Add");
            btn.setBounds(20, 110, 100, 30);
            btn.addActionListener(e -> {
                try {
                    double amt = Double.parseDouble(amountField.getText());
                    oos.writeInt(5);
                    oos.writeDouble(amt);
                    oos.flush();
                    JOptionPane.showMessageDialog(this, "Funds Added");

                    dispose();
                    parentMenu.dispose();
                    new CustomerMenuGUI(parentMenu.ois, parentMenu.oos);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Invalid Amount");
                }
            });
            add(btn);
            setVisible(true);
        }
    }

    private class UserSettingsGUI extends JFrame { // Screen to edit profile/delete account
        ObjectInputStream ois;
        ObjectOutputStream oos;

        public UserSettingsGUI(ObjectInputStream ois, ObjectOutputStream oos) {
            this.ois = ois;
            this.oos = oos;
            init();
        }

        private void init() {
            setTitle("Settings");
            setSize(400, 400);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(null);

            JButton nameBtn = new JButton("Change Name");
            nameBtn.setBounds(85, 80, 220, 40);
            nameBtn.addActionListener(e -> changeName());
            add(nameBtn);

            JButton passBtn = new JButton("Change Password");
            passBtn.setBounds(85, 140, 220, 40);
            passBtn.addActionListener(e -> changePass());
            add(passBtn);

            JButton delBtn = new JButton("Delete Account");
            delBtn.setBounds(85, 200, 220, 40);
            delBtn.setBackground(Color.RED);
            delBtn.addActionListener(e -> deleteAcc());
            add(delBtn);

            setVisible(true);
        }

        private void changeName() {
            String f = JOptionPane.showInputDialog("First Name:");
            String l = JOptionPane.showInputDialog("Last Name:");
            if (f == null || l == null) return;
            try {
                oos.writeInt(6);
                oos.writeInt(1);
                oos.writeObject(f);
                oos.writeObject(l);
                oos.flush();
                ois.readObject();
                ois.readObject();
                ois.readDouble();
                JOptionPane.showMessageDialog(this, "Name Changed");
                dispose();
                new CustomerMenuGUI(ois, oos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void changePass() {
            String p1 = JOptionPane.showInputDialog("New Password:");
            String p2 = JOptionPane.showInputDialog("Confirm:");
            if (p1 == null || !p1.equals(p2)) return;
            try {
                oos.writeInt(6);
                oos.writeInt(2);
                oos.writeObject(p1);
                oos.writeObject(p2);
                oos.flush();
                ois.readObject();
                ois.readObject();
                ois.readDouble();
                JOptionPane.showMessageDialog(this, "Password Changed");
                dispose();
                new CustomerMenuGUI(ois, oos);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void deleteAcc() {
            int res = JOptionPane.showConfirmDialog(this, "Delete Account?", "Warn", JOptionPane.YES_NO_OPTION);
            if (res == JOptionPane.YES_OPTION) {
                try {
                    oos.writeInt(6);
                    oos.writeInt(3);
                    oos.writeBoolean(true);
                    oos.flush();

                    for (Window w : Window.getWindows()) {
                        w.dispose();
                    }

                    new AuthSelector(ois, oos);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
