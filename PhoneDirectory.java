import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Interface for file operations
interface FileOperations {
    void readData() throws IOException;
    void writeData() throws IOException;
}

// User class representing a phone directory entry
class User {
    private String name;
    private String phoneNumber;

    public User(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}

// Base class for GUI frame
class BaseFrame extends JFrame {
    protected JTable table;
    protected DefaultTableModel tableModel;

    public BaseFrame(String title) {
        super(title);

        // Create table model with column names
        String[] columnNames = {"Name", "Phone Number"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // Create table with the table model
        table = new JTable(tableModel);

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Add scroll pane to the frame
        add(scrollPane, BorderLayout.CENTER);
    }

    // Method to be implemented by derived classes
    protected void addUser() {
        throw new UnsupportedOperationException();
    }

    // Method to be implemented by derived classes
    protected void searchUser() {
        throw new UnsupportedOperationException();
    }
}

// Derived class for main application frame
class MainFrame extends BaseFrame implements FileOperations {
    private File file;
    private List<User> users;

    public MainFrame(String title, String fileName) {
        super(title);
        file = new File(fileName);
        users = new ArrayList<>();
        try {
            readData();
            displayUsers();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading data from file", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Event listener for table row selection
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow != -1) {
                        showUserPopup(selectedRow);
                    }
                }
            }
        });
    }

    // Implement interface methods
    public void readData() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 2) {
                String name = parts[0].trim();
                String phoneNumber = parts[1].trim();
                User user = new User(name, phoneNumber);
                users.add(user);
            }
        }
        reader.close();
    }

    public void writeData() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        for (User user : users) {
            writer.write(user.getName() + ", " + user.getPhoneNumber() + "\n");
        }
        writer.close();
    }

    // Override method for adding a user
    protected void addUser() {
        String name = JOptionPane.showInputDialog(this, "Enter Name:");
        String phoneNumber = JOptionPane.showInputDialog(this, "Enter Phone Number:");
        if (name != null && phoneNumber != null) {
            User user = new User(name, phoneNumber);
            users.add(user);
            displayUsers();
            try {
                writeData(); // Save data to file
                JOptionPane.showMessageDialog(this, "Data saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving data to file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Override method for searching a user
    protected void searchUser() {
        String searchName = JOptionPane.showInputDialog(this, "Enter Name to Search:");
        if (searchName != null) {
            List<User> searchResults = new ArrayList<>();
            for (User user : users) {
                if (user.getName().equalsIgnoreCase(searchName)) {
                    searchResults.add(user);
                }
            }
            displayUsers(searchResults);
        }
    }

    // Display users in the table
    private void displayUsers(List<User> displayUsers) {
        tableModel.setRowCount(0); // Clear existing rows
        for (User user : displayUsers) {
            Object[] rowData = {user.getName(), user.getPhoneNumber()};
            tableModel.addRow(rowData);
        }
    }

    // Display all users in the table
    void displayUsers() {
        displayUsers(users);
    }

    // Show a popup with user details
    private void showUserPopup(int rowIndex) {
        User user = users.get(rowIndex);
        JTextField nameField = new JTextField(user.getName());
        JTextField phoneNumberField = new JTextField(user.getPhoneNumber());

        Object[] message = {
                "Name:", nameField,
                "Phone Number:", phoneNumberField
        };

        int option = JOptionPane.showOptionDialog(
                this,
                message,
                "User Details",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"Save", "Delete"},
                "Save"
        );

        if (option == JOptionPane.OK_OPTION) {
            user.setName(nameField.getText());
            user.setPhoneNumber(phoneNumberField.getText());
            displayUsers();
            try {
                writeData(); // Save data to file
                JOptionPane.showMessageDialog(this, "Data saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving data to file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (option == 1) { // Delete button pressed
            int confirmOption = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this user?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirmOption == JOptionPane.YES_OPTION) {
                users.remove(user);
                displayUsers();
                try {
                    writeData(); // Save data to file
                    JOptionPane.showMessageDialog(this, "User deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error saving data to file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}

public class PhoneDirectory {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        MainFrame frame = new MainFrame("Phone Directory", "random_data.txt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add buttons and search field to a panel
        JPanel buttonPanel = new JPanel();
        JButton homeButton = new JButton("Home"); // Add Home button
        JButton addButton = new JButton("Add User");
        JButton searchButton = new JButton("Search");
        buttonPanel.add(homeButton);
        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Event listener for Home button
        homeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.displayUsers();
            }
        });

        // Event listener for add button
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.addUser();
            }
        });

        // Event listener for search button
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.searchUser();
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
