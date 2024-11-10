package patientinfo;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Doctor extends JFrame {

    private static final long serialVersionUID = 1L;
    private ImagePanel contentPane;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Doctor frame = new Doctor();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Doctor() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1071, 671);

        contentPane = new ImagePanel("C:\\Users\\DEV\\OneDrive\\Desktop\\2.jpg"); // Specify the path to your image
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel lblNewLabel = new JLabel("  Select Your Medical Problem");
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 40));
        lblNewLabel.setForeground(new Color(255, 255, 255));
        lblNewLabel.setBounds(26, 38, 977, 125);
        contentPane.add(lblNewLabel);

        JComboBox<String> medicalList = new JComboBox<>();
        medicalList.setBounds(59, 194, 970, 43);
        contentPane.add(medicalList);

        // Call the method to connect and populate the JComboBox
        connectAndPopulateComboBox(medicalList);
    }

    private void connectAndPopulateComboBox(JComboBox<String> comboBox) {
        try {
            // Load the JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Connect to the SQLite database
            Connection connection = DriverManager.getConnection("jdbc:sqlite:D:\\Brunel\\group project all documnent\\medical.db");

            // Execute a query to retrieve data
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT descriptions FROM medical");
            ResultSet resultSet = preparedStatement.executeQuery();

            // Populate the JComboBox with the data
            DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) comboBox.getModel();
            while (resultSet.next()) {
                String columnName = resultSet.getString("descriptions");
                model.addElement(columnName);
            }

            // Close resources
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Custom JPanel with a background image
    static class ImagePanel extends JPanel {
        private Image backgroundImage;

        public ImagePanel(String imagePath) {
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
