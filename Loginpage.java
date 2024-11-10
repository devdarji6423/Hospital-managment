package patientinfo; // Package declaration

import java.awt.BorderLayout; // Importing necessary libraries
import java.awt.EventQueue;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JTextField;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import javax.swing.border.LineBorder;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

public class Loginpage extends JFrame { // Class declaration for Loginpage, extending JFrame
    // Database URL
    private static final String DATABASE_URL = "jdbc:sqlite:D:\\Brunel\\group project all documnent\\new loginpage data.db";
    // Serial version UID for serialization
    private static final long serialVersionUID = 1L;
    
    // Instance variables
    private JPanel contentPane;
    private JPasswordField password;
    private JTextField Password1;
    private JTextField username;

    // Main method
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Loginpage frame = new Loginpage(); // Creating instance of Loginpage
                    frame.setVisible(true); // Making the frame visible
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Constructor
    public Loginpage() {
        // Setting JFrame properties
        setBackground(new Color(240, 240, 240));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1184, 718);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(0, 0, 0));
        contentPane.setForeground(new Color(128, 255, 128));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Hospital name label
        JLabel lblNewLabel = new JLabel("      Brunel Multi-Speciality Hospital");
        lblNewLabel.setBounds(10, 10, 984, 135);
        lblNewLabel.setForeground(new Color(102, 204, 255));
        lblNewLabel.setIcon(new ImageIcon("D:\\Brunel\\group project all documnent\\medical-hospital-flat-icon-on-260nw-555407245.jpg"));
        lblNewLabel.setFont(new Font("Arial", Font.BOLD, 40));
        contentPane.add(lblNewLabel);

        // Login button
        JButton login = new JButton("login");
        login.setBounds(422, 561, 199, 45);
        login.setBorder(new LineBorder(new Color(255, 255, 255), 3, true));
        login.setBackground(new Color(0, 0, 0));
        login.setForeground(new Color(255, 255, 255));
        login.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0){
                String usr = username.getText(); // Get username from text field
                String psd = Password1.getText(); // Get password from text field
                if (checkCredentials(usr, psd)) { // Call method to check credentials
                    JOptionPane.showMessageDialog(contentPane,"you are successfully logged in "); // Show success message
                    Home Home = new Home(); // Create instance of Home page
                    setVisible(false); // Hide current login page
                    Home.setVisible(true); // Show home page
                } else {
                    JOptionPane.showMessageDialog(contentPane,"Invalid username or password "); // Show error message
                }
            }
        });
        login.setFont(new Font("Tahoma", Font.PLAIN, 30));
        contentPane.add(login);

        // Reset button
        JButton Reset = new JButton("Reset");
        Reset.setBounds(422, 484, 199, 45);
        Reset.setHideActionText(true);
        Reset.setBorder(new LineBorder(new Color(255, 255, 255), 3, true));
        Reset.setBackground(new Color(0, 0, 0));
        Reset.setForeground(new Color(255, 255, 255));
        Reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                username.setText(""); // Clear username text field
                Password1.setText(""); // Clear password text field
            }
        });
        Reset.setFont(new Font("Tahoma", Font.PLAIN, 30));
        contentPane.add(Reset);

        // New user registration button
        JButton btnNewButton = new JButton("Click here for new user");
        btnNewButton.setBorder(new LineBorder(new Color(255, 255, 255), 0, true));
        btnNewButton.setBounds(405, 431, 243, 21);
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Newuser newuser = new Newuser(); // Create instance of new user registration page
                newuser.setVisible(true); // Show new user registration page
            }
        });
        btnNewButton.setForeground(new Color(0, 255, 0));
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
        btnNewButton.setBorderPainted(false);
        btnNewButton.setBackground(new Color(0, 0, 0));
        contentPane.add(btnNewButton);

        // Password field
        Password1 = new JTextField();
        Password1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        Password1.setBounds(422, 345, 206, 45);
        contentPane.add(Password1);
        Password1.setColumns(10);

        // Username field
        username = new JTextField();
        username.setFont(new Font("Tahoma", Font.PLAIN, 15));
        username.setBounds(422, 237, 206, 45);
        contentPane.add(username);
        username.setColumns(10);

        // Username label
        JLabel lblNewLabel_1 = new JLabel("Username");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblNewLabel_1.setForeground(new Color(255, 255, 255));
        lblNewLabel_1.setBounds(422, 195, 122, 21);
        contentPane.add(lblNewLabel_1);

        // Password label
        JLabel lblNewLabel_2 = new JLabel("Password");
        lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblNewLabel_2.setForeground(new Color(255, 255, 255));
        lblNewLabel_2.setBounds(422, 305, 105, 21);
        contentPane.add(lblNewLabel_2);

        // Setting focus traversal policy
        //This policy defines the order in which focus moves between components when the user presses the Tab key.
        contentPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{lblNewLabel, login, Reset, btnNewButton, Password1, username, lblNewLabel_1, lblNewLabel_2}));
    }

    // Method to check user credentials
    private boolean checkCredentials(String username, String password) {
        try {
            // Establishing connection to the database
            Connection connection = DriverManager.getConnection(DATABASE_URL);
            String query = "SELECT * FROM users WHERE username = ? AND password = ?"; // The '?' placeholders in the SQL query indicate where parameters should be inserted.  // username and password we have column in table
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                // Setting parameters in the SQL query
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    return resultSet.next(); // Returns true if a matching record is found
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
