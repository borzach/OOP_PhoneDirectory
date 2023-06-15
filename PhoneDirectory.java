import javax.swing.*;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

// Base class for GUI frame
class BaseFrame extends JFrame {
    protected JTextArea textArea;
    protected JButton saveButton;
    protected JButton addButton;
    protected JButton searchButton;

    public BaseFrame(String title) {
        super(title);

        // Create components
        textArea = new JTextArea();
        saveButton = new JButton("Save");
        addButton = new JButton("Add User");
        searchButton = new JButton("Search");

        // Set layout
        setLayout(new BorderLayout());

        // Create panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(addButton);
        buttonPanel.add(searchButton);

        // Add components to the frame
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listener for save button
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    saveDataToFile();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(BaseFrame.this, "Error saving data to file", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Event listener for add button
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addUser();
            }
        });

        // Event listener for search button
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchUser();
            }
        });
    }

    // Method to be implemented by derived classes
    protected void saveDataToFile() throws IOException {
        throw new UnsupportedOperationException();
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

    // Override method for saving data to file
    protected void saveDataToFile() throws IOException {
        writeData();
        JOptionPane.showMessageDialog(this, "Data saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Override method for adding a user
    protected void addUser() {
        String name = JOptionPane.showInputDialog(this, "Enter Name:");
        String phoneNumber = JOptionPane.showInputDialog(this, "Enter Phone Number:");
        if (name != null && phoneNumber != null) {
            User user = new User(name, phoneNumber);
            users.add(user);
            displayUsers();
        }
    }

    // Override method for searching a user
    protected void searchUser() {
        String searchName = JOptionPane.showInputDialog(this, "Enter Name to Search:");
        if (searchName != null) {
            boolean found = false;
            for (User user : users) {
                if (user.getName().equalsIgnoreCase(searchName)) {
                    found = true;
                    String message = "Name: " + user.getName() + "\nPhone Number: " + user.getPhoneNumber();
                    JOptionPane.showMessageDialog(this, message, "User Found", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
            if (!found) {
                JOptionPane.showMessageDialog(this, "User not found", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Display all users in the text area
    private void displayUsers() {
        StringBuilder content = new StringBuilder();
        for (User user : users) {
            content.append(user.getName()).append(", ").append(user.getPhoneNumber()).append("\n");
        }
        textArea.setText(content.toString());
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
        MainFrame frame = new MainFrame("Phone Directory", "phone_directory.txt");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
