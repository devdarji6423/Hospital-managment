package patientinfo;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import java.awt.Color;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;
import javax.swing.JCheckBox;

public class KinInformation extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField HouseNumber;
	private JTextField StreetName;
	private JTextField CityorTown;
	private JTextField postalcode;
	private JComboBox comboBox;
	private JComboBox<String> medicalproblem;
	 private JComboBox Disability;
    private JFormattedTextField formattedTextField_1;
    private JFormattedTextField birthday;
    private int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();}
	private static final String DATABASE_URL = "jdbc:sqlite:D:\\Brunel\\group project all documnent\\Kininforamtion.db";
	private JTextField textField_2;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					KinInformation frame = new KinInformation();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	

	/**
	 * Create the frame.
	 */
	public KinInformation() {
		setBackground(new Color(0, 0, 0));
		setTitle("Kin Information");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1071, 669);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(0, 0, 0));
		contentPane.setBackground(new Color(0, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Kin Information");
		lblNewLabel.setForeground(new Color(0, 0, 0));
		lblNewLabel.setBackground(new Color(0, 0, 0));
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 50));
		lblNewLabel.setBounds(566, 242, 816, 68);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("First Name");
		lblNewLabel_1.setForeground(new Color(0, 0, 0));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1.setBounds(42, 190, 158, 31);
		contentPane.add(lblNewLabel_1);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 20));
		textField.setSelectedTextColor(new Color(255, 255, 255));
		textField.setDisabledTextColor(new Color(0, 0, 0));
		textField.setForeground(new Color(0, 0, 0));
		textField.setBackground(new Color(255, 255, 255));
		textField.setBounds(259, 190, 274, 31);
		contentPane.add(textField);
		textField.setColumns(10);
		// Medical Problem Combo Box
        medicalproblem = new JComboBox<>();
        medicalproblem.setFont(new Font("Tahoma", Font.PLAIN, 15));
        medicalproblem.setBounds(42, 73, 502, 34);
        contentPane.add(medicalproblem);
        // Call the method to connect and populate the JComboBox
        connectAndPopulateComboBox(medicalproblem);
        
		comboBox = new JComboBox();
		comboBox.setFont(new Font("Tahoma", Font.PLAIN, 15));
		comboBox.setForeground(new Color(0, 0, 0));
		comboBox.setBackground(new Color(255, 255, 255));
		comboBox.setBounds(189, 190, 71, 31);
		contentPane.add(comboBox);
		comboBox.addItem("Mr.");
		comboBox.addItem("Miss.");
		
		JLabel lblNewLabel_1_1 = new JLabel("Last Name");
		lblNewLabel_1_1.setForeground(new Color(0, 0, 0));
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1_1.setBounds(42, 258, 158, 31);
		contentPane.add(lblNewLabel_1_1);
		
		textField_1 = new JTextField();
		textField_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		textField_1.setForeground(new Color(0, 0, 0));
		textField_1.setBackground(new Color(255, 255, 255));
		textField_1.setColumns(10);
		textField_1.setBounds(189, 258, 344, 31);
		contentPane.add(textField_1);
		
		JLabel lblNewLabel_1_1_1 = new JLabel("Date of Birth");
		lblNewLabel_1_1_1.setForeground(new Color(0, 0, 0));
		lblNewLabel_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1_1_1.setBounds(42, 322, 158, 31);
		contentPane.add(lblNewLabel_1_1_1);
		
		
		birthday = new JFormattedTextField();
		birthday.setFont(new Font("Tahoma", Font.PLAIN, 20));
		birthday.setForeground(new Color(0, 0, 0));
		birthday.setBackground(new Color(255, 255, 255));
		birthday.setBounds(189, 322, 344, 31);
		contentPane.add(birthday);
		try {
            // Create a MaskFormatter for the desired format (in this case, a date format)
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            dateFormatter.setPlaceholderCharacter('_'); // Optional: Set a placeholder character

            // Set the MaskFormatter to the JFormattedTextField
            birthday.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(dateFormatter));
            
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
		
		JLabel lblNewLabel_1_1_1_1 = new JLabel("Telephone No.");
		lblNewLabel_1_1_1_1.setForeground(new Color(0, 0, 0));
        lblNewLabel_1_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
        lblNewLabel_1_1_1_1.setBounds(42, 385, 158, 31);
        contentPane.add(lblNewLabel_1_1_1_1);
        
        try {
            // Create a MaskFormatter for the phone number format
            MaskFormatter phoneFormatter = new MaskFormatter("+44"+"##########");
            phoneFormatter.setPlaceholderCharacter('_'); // Optional: Set a placeholder character

            // Create a JFormattedTextField with the specified MaskFormatter
            formattedTextField_1 = new JFormattedTextField(phoneFormatter);
            formattedTextField_1.setText("+44        ");
            formattedTextField_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
            formattedTextField_1.setBackground(new Color(255, 255, 255));
            formattedTextField_1.setForeground(new Color(0, 0, 0));
            formattedTextField_1.setBounds(189, 385, 344, 31);
            contentPane.add(formattedTextField_1);
            
            JButton btnNewButton = new JButton("submit");
            btnNewButton.setBorder(new LineBorder(new Color(0, 0, 0), 3));
            btnNewButton.setBackground(new Color(255, 255, 255));
            btnNewButton.setForeground(new Color(0, 0, 0));
            btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
            btnNewButton.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		// Validate first name (no integers allowed)
                    String firstName = textField.getText().trim();
                    if (containsDigit(firstName)) {
                        showError("First name cannot contain numbers");
                        return;
                    }
                    if (firstName.isEmpty()) {
                        showError("First name cannot be empty");
                        return;
                    }
                    
                    

                    // Validate last name (no integers allowed)
                    String lastName = textField_1.getText().trim();
                    if (lastName.isEmpty()) {
                        showError("Last name cannot be empty");
                        return;
                    }
                    if (containsDigit(lastName)) {
                        showError("Last name cannot contain numbers");
                        return;
                    }
                    String dob = birthday.getText().trim();
                    String dobPlaceholder = "__/__/____";
                    if (dob.equals(dobPlaceholder)) {
                        showError("Date of birth cannot be empty");
                        return;
                    }
                    // Validate telephone number
                    String phoneNumber = formattedTextField_1.getText().trim();
                    if (phoneNumber.isEmpty()) {
                        showError("Telephone number cannot be empty");
                        return;
                    }
                    // Validate house number (should be an integer)
                    String houseNumberStr = HouseNumber.getText().trim();
                    if (!isInteger(houseNumberStr)) {
                        showError("House number should be an integer");
                        return;
                    }

                    // Validate street name (should be a string)
                    String streetName = StreetName.getText().trim();
                    if (streetName.isEmpty()) {
                        showError("Street name cannot be empty");
                        return;
                    }

                    // Validate city or town (should be a string)
                    String cityOrTown = CityorTown.getText().trim();
                    if (cityOrTown.isEmpty()) {
                        showError("City or town cannot be empty");
                        return;
                    }

                    // Validate postal code (should be unique and not empty)
                    String postalCode = postalcode.getText().trim();
                    if (postalCode.isEmpty() ||postalCode.length()<6||postalCode.length()>7||isPostalCodeExists(postalCode)) {
                        showError("Postal code should be unique, not empty,and have a length between 6 and 7");
                        return;
                    }
                    
                    if (streetName.equalsIgnoreCase(cityOrTown)) {
                        showError("Street name and city or town should not be the same");
                        return;
                    }

                    if (streetName.equalsIgnoreCase(postalCode)) {
                        showError("Street name and postal code should not be the same");
                        return;
                    }

                    if (cityOrTown.equalsIgnoreCase(postalCode)) {
                        showError("City or town and postal code should not be the same");
                        return;
                    }
                    // If validation passes, you can proceed to the next frame or perform other actions
                    Home home = new Home();
                    home.setVisible(true);
                    if (storeUserData()) {
                        JOptionPane.showMessageDialog(contentPane, "Your data is stored successfully!");
                    } else {
                        JOptionPane.showMessageDialog(contentPane, "Failed to store data.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            	}
            );
            btnNewButton.setBounds(381, 581, 152, 41);
            contentPane.add(btnNewButton);
            
            JLabel lblNewLabel_1_1_1_1_1_1 = new JLabel("House No.");
            lblNewLabel_1_1_1_1_1_1.setForeground(new Color(0, 0, 0));
            lblNewLabel_1_1_1_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
            lblNewLabel_1_1_1_1_1_1.setBounds(42, 454, 158, 31);
            contentPane.add(lblNewLabel_1_1_1_1_1_1);
            
            HouseNumber = new JTextField();
            HouseNumber.setFont(new Font("Tahoma", Font.PLAIN, 20));
            HouseNumber.setForeground(new Color(0, 0, 0));
            HouseNumber.setBackground(new Color(255, 255, 255));
            HouseNumber.setColumns(10);
            HouseNumber.setBounds(189, 454, 344, 31);
            contentPane.add(HouseNumber);
            
            JLabel lblNewLabel_1_1_1_1_1_2 = new JLabel("Street Name");
            lblNewLabel_1_1_1_1_1_2.setForeground(new Color(0, 0, 0));
            lblNewLabel_1_1_1_1_1_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
            lblNewLabel_1_1_1_1_1_2.setBounds(42, 523, 158, 31);
            contentPane.add(lblNewLabel_1_1_1_1_1_2);
            
            StreetName = new JTextField();
            StreetName.setFont(new Font("Tahoma", Font.PLAIN, 20));
            StreetName.setForeground(new Color(0, 0, 0));
            StreetName.setBackground(new Color(255, 255, 255));
            StreetName.setColumns(10);
            StreetName.setBounds(189, 523, 344, 31);
            contentPane.add(StreetName);
            
            JLabel lblNewLabel_1_1_1_1_1_2_1 = new JLabel("City or Town");
            lblNewLabel_1_1_1_1_1_2_1.setForeground(new Color(0, 0, 0));
            lblNewLabel_1_1_1_1_1_2_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
            lblNewLabel_1_1_1_1_1_2_1.setBounds(566, 454, 158, 31);
            contentPane.add(lblNewLabel_1_1_1_1_1_2_1);
            
            CityorTown = new JTextField();
            CityorTown.setFont(new Font("Tahoma", Font.PLAIN, 20));
            CityorTown.setForeground(new Color(0, 0, 0));
            CityorTown.setBackground(new Color(255, 255, 255));
            CityorTown.setColumns(10);
            CityorTown.setBounds(703, 454, 344, 31);
            contentPane.add(CityorTown);
            
            JLabel lblNewLabel_1_1_1_1_1_2_1_1 = new JLabel("Postal Code");
            lblNewLabel_1_1_1_1_1_2_1_1.setForeground(new Color(0, 0, 0));
            lblNewLabel_1_1_1_1_1_2_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
            lblNewLabel_1_1_1_1_1_2_1_1.setBounds(561, 523, 158, 31);
            contentPane.add(lblNewLabel_1_1_1_1_1_2_1_1);
            
            postalcode = new JTextField();
            postalcode.setFont(new Font("Tahoma", Font.PLAIN, 20));
            postalcode.setForeground(new Color(0, 0, 0));
            postalcode.setBackground(new Color(255, 255, 255));
            postalcode.setColumns(10);
            postalcode.setBounds(703, 523, 344, 31);
            contentPane.add(postalcode);
            
            JLabel lblNewLabel_2 = new JLabel("Select Your medical Problem");
            lblNewLabel_2.setForeground(new Color(0, 0, 0));
            lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
            lblNewLabel_2.setBackground(Color.BLACK);
            lblNewLabel_2.setBounds(42, 23, 329, 39);
            contentPane.add(lblNewLabel_2);
            
            JButton btnNewButton_1 = new JButton("Add new Medical Problem");
            btnNewButton_1.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		Newmedical Newmedical=new Newmedical();
            		Newmedical.setVisible(true);
            	}
            });
            btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 15));
            btnNewButton_1.setBounds(578, 76, 246, 28);
            contentPane.add(btnNewButton_1);
            
            textField_2 = new JTextField();
            textField_2.setText(" Disability");
            textField_2.setForeground(new Color(0, 0, 0));
            textField_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
            textField_2.setColumns(10);
            textField_2.setBorder(null);
            textField_2.setBackground(new Color(0, 255, 255));
            textField_2.setBounds(42, 127, 109, 43);
            contentPane.add(textField_2);
            
            Disability = new JComboBox();
            Disability.setFont(new Font("Tahoma", Font.PLAIN, 15));
            Disability.setBounds(189, 132, 344, 31);
            contentPane.add(Disability);
            Disability.addItem("No");
            Disability.addItem("Vision Impairment");
            Disability.addItem("Deaf or hard of hearing");
            Disability.addItem("Mental health conditions");
            Disability.addItem("Intellectual disability");
            Disability.addItem("Acquired brain injury");
            Disability.addItem("Autism spectrum disorder");
            Disability.addItem("Wheelchair user");

        } catch (ParseException e) {
            e.printStackTrace();
        }
	}
	// it is a pop up message class to check the first name, last name
        private void showError(String message) {
            JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
        }
        private boolean isInteger(String s) {
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        private boolean isPostalCodeExists(String postalCode) {
            // Add logic to check if the postal code already exists
            // Return true if it exists, false otherwise
            return false;
        }
        
        private boolean containsDigit(String s) {
            for (char c : s.toCharArray()) {
                if (Character.isDigit(c)) {
                    return true;
                }
            }
            return false;}
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
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) this.medicalproblem.getModel();
                while (resultSet.next()) {
                    String columnName = resultSet.getString("descriptions");
                    model.addElement(columnName);
                }

                // Close resources
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (Exception e) {
                e.printStackTrace();}
            }
            private boolean storeUserData() {
                try {
                    Connection connection = DriverManager.getConnection(DATABASE_URL);
                    // Generate a random 8-digit visiting number
                    String visitingNumber = generateRandomVisitingNumber();
                    
                    String insertPatientQuery = "INSERT INTO kininformation (visiting_number,medicalproblem,title,Disability, first_name, last_name,Email_ID, dob, phone_number,"
                    		+"address, city, postal_code) VALUES (?,?,?,?, ?, ?, ?, ?, ?, ?, ?,?)";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertPatientQuery)) {
                    	String selectedMedicalProblem = this.medicalproblem.getSelectedItem().toString();
                    	String title = comboBox.getSelectedItem().toString();
                        String selectedDisability=Disability.getSelectedItem().toString();                        
                        String firstName = textField.getText().trim();
                        String lastName = textField_1.getText().trim();
                        String EmailID = textField_2.getText().trim();
                        String dob = birthday.getText().trim();
                        String phoneNumber = formattedTextField_1.getText().trim();
                        String address = HouseNumber.getText().trim() + " " + StreetName.getText().trim();
                        String city = CityorTown.getText().trim();
                        String postalCode = postalcode.getText().trim();
                        
                        preparedStatement.setString(1, visitingNumber);
                        preparedStatement.setString(2, selectedMedicalProblem);
                        preparedStatement.setString(3, title);
                        preparedStatement.setString(4, selectedDisability);
                        preparedStatement.setString(5, firstName);
                        preparedStatement.setString(6, lastName);
                        preparedStatement.setString(7, EmailID);
                        preparedStatement.setString(8, dob);
                        preparedStatement.setString(9, phoneNumber);
                        preparedStatement.setString(10, address);
                        preparedStatement.setString(11, city);
                        preparedStatement.setString(12, postalCode);
                        preparedStatement.executeUpdate();
                    }
                 // Calculate age based on the entered birth date
                    String dobText = birthday.getText().trim();
                    LocalDate birthDate = LocalDate.parse(dobText, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    int age = calculateAge(birthDate);

                    // Display appropriate message based on age
                    String ageMessage;
                    if (age >= 70) {
                        ageMessage = "You are eligible for a free meal once you visit the hospital.";
                    } else if (age < 18) {
                        ageMessage = "Please ensure you are accompanied by an adult when visiting the hospital.";
                    } else if (age < 13) {
                        ageMessage = "You will be assigned a doctor who is specialized in pediatrics.";
                    } else {
                        ageMessage = "You are welcome to visit the hospital.";
                    }

                 // Show a pop-up message with the visiting number and age calculation message
                    JOptionPane.showMessageDialog(this, "Your data is stored successfully!\nVisiting Number: " + visitingNumber +
                            "\n\nAge Notification:\n" + ageMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
                    connection.close();
                    return true;
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    return false;
                }
            
        }
            // Generate a random 8-digit visiting number
            private String generateRandomVisitingNumber() {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < 8; i++) {
                    int digit = (int) (Math.random() * 10);
                    sb.append(digit);
                }
                return sb.toString();
}}

