package patientinfo;

import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;
import javax.swing.border.LineBorder;

public class Newmedical extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtNeewMedicalProblem;
    private static final String DATABASE_URL = "jdbc:sqlite:D:\\Brunel\\group project all documnent\\medical.db";

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Newmedical frame = new Newmedical();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Newmedical() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 703, 441);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(0, 0, 0));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // Text field for entering new medical problems
        txtNeewMedicalProblem = new JTextField();
        txtNeewMedicalProblem.setForeground(new Color(255, 255, 255));
        txtNeewMedicalProblem.setBackground(new Color(0, 0, 0));
        txtNeewMedicalProblem.setBounds(309, 115, 229, 30);
        contentPane.add(txtNeewMedicalProblem);
        txtNeewMedicalProblem.setColumns(10);

        // Label for new medical problem text field
        JLabel lblNewLabel = new JLabel("New Medical Problem");
        lblNewLabel.setForeground(new Color(0, 255, 0));
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblNewLabel.setBounds(68, 115, 215, 32);
        contentPane.add(lblNewLabel);

        // Add key listener to text field to handle Enter key press
        txtNeewMedicalProblem.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                // Check if the pressed key is Enter
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Perform the action when Enter is pressed
                    submitAction();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    // Method to submit new medical problem
    private void submitAction() {
        try {
            // Load the JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Establish a connection
            try (Connection connection = DriverManager.getConnection(DATABASE_URL)) {
                // Get the medical problem from the text field
                String medicalProblem = txtNeewMedicalProblem.getText();

                // SQL query to check if the data already exists
                String checkQuery = "SELECT * FROM medical WHERE LOWER(descriptions) = ?";
                try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                    // Set the medical problem to lowercase for case-insensitive comparison
                    checkStatement.setString(1, medicalProblem.toLowerCase());

                    // Execute the query to check if the data exists
                    try (ResultSet resultSet = checkStatement.executeQuery()) {
                        if (resultSet.next()) {
                            // Data already exists, show an error message
                            JOptionPane.showMessageDialog(null, "Error: Data already exists in the database.",
                                    "Duplicate Entry", JOptionPane.ERROR_MESSAGE);
                        } else {
                            // Data doesn't exist, proceed with insertion

                            // SQL query to insert data into the 'medical' table
                            String insertQuery = "INSERT INTO medical(descriptions) VALUES (?)";

                            // Use a prepared statement to avoid SQL injection
                            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                                insertStatement.setString(1, medicalProblem);

                                // Execute the query for insertion
                                insertStatement.executeUpdate();

                                // Close the resources for insertion
                            }

                            // Open the Patientinformation frame or perform any other action
                            Patientinformation add = new Patientinformation();
                            add.setVisible(true);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }
}
