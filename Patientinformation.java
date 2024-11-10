package patientinfo;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.MaskFormatter;

import java.awt.Color;
import org.eclipse.wb.swing.FocusTraversalOnArray;

import patientinfo.Doctor.ImagePanel;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.text.ParseException;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.Period;
import javax.swing.JOptionPane;
import java.time.format.DateTimeFormatter;


public class Patientinformation extends JFrame {
	// instance variable declared for components used in the GUI
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField firstname;
	private JTextField lastname;
	private JTextField HouseNumber;
	private JTextField StreetName;
	private JTextField CityorTown;
	private JTextField postalcode;
	private JComboBox title1;
    private JFormattedTextField phoneno;
    private JFormattedTextField birthday;
    private JComboBox<String> medicalproblem;
    private JComboBox Disability;
    // to calculate 
    private int calculateAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }
    
    // Database URL
	private static final String DATABASE_URL = "jdbc:sqlite:D:\\Brunel\\group project all documnent\\patientinformationdata.db";
	private JTextField Email;
	private JTextField txtDisability;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Patientinformation frame = new Patientinformation();
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
	public Patientinformation() {
		
		// JFrame setup
		setBackground(new Color(0, 0, 0));
		setTitle("Patient Information");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1184, 718);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Patient Information");
		lblNewLabel.setBounds(661, 255, 816, 68);
		lblNewLabel.setForeground(new Color(0, 0, 0));
		lblNewLabel.setBackground(new Color(0, 0, 0));
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 50));
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("First Name");
		lblNewLabel_1.setBounds(31, 231, 158, 31);
		lblNewLabel_1.setForeground(new Color(0, 0, 0));
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPane.add(lblNewLabel_1);
		
		// First Name text field
		firstname = new JTextField();
		firstname.setBorder(null);
		firstname.setBounds(259, 231, 284, 31);
		firstname.setFont(new Font("Tahoma", Font.PLAIN, 20));
		firstname.setSelectedTextColor(new Color(255, 255, 255));
		firstname.setDisabledTextColor(new Color(0, 0, 0));
		firstname.setForeground(new Color(0, 0, 0));
		firstname.setBackground(new Color(255, 255, 255));
		contentPane.add(firstname);
		firstname.setColumns(10);
		 // Title Combo Box
		title1 = new JComboBox();
		title1.setFont(new Font("Tahoma", Font.PLAIN, 15));
		title1.setBorder(null);
		title1.setBounds(189, 231, 71, 31);
		title1.setForeground(new Color(0, 0, 0));
		title1.setBackground(new Color(255, 255, 255));
		contentPane.add(title1);
		title1.addItem("Mr.");
		title1.addItem("Miss.");
		
		
		// Last Name label
		JLabel lblNewLabel_1_1 = new JLabel("Last Name");
		lblNewLabel_1_1.setBounds(31, 286, 158, 31);
		lblNewLabel_1_1.setForeground(new Color(0, 0, 0));
		lblNewLabel_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPane.add(lblNewLabel_1_1);
		
		 // Last Name text field
		lastname = new JTextField();
		lastname.setBorder(null);
		lastname.setBounds(189, 286, 354, 31);
		lastname.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lastname.setForeground(new Color(0, 0, 0));
		lastname.setBackground(new Color(255, 255, 255));
		lastname.setColumns(10);
		contentPane.add(lastname);
		
		// Date of Birth label
		JLabel lblNewLabel_1_1_1 = new JLabel("Date of Birth");
		lblNewLabel_1_1_1.setBounds(31, 402, 158, 31);
		lblNewLabel_1_1_1.setForeground(new Color(0, 0, 0));
		lblNewLabel_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		contentPane.add(lblNewLabel_1_1_1);
		
		 // Date of Birth formatted text field
		birthday = new JFormattedTextField();
		birthday.setBorder(null);
		birthday.setBounds(189, 402, 354, 31);
		birthday.setFont(new Font("Tahoma", Font.PLAIN, 20));
		birthday.setForeground(new Color(0, 0, 0));
		birthday.setBackground(new Color(255, 255, 255));
		contentPane.add(birthday);
		try {
            // Create a MaskFormatter for the desired format (in this case, a date format)
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####");
            //This line sets a placeholder character ('_') for any empty spaces in the formatted text. It means that if the user doesn't fill in all parts of the date (for example, only entering the month and day but not the year), the empty spaces will be represented by underscores.
            dateFormatter.setPlaceholderCharacter('_'); 

            // Set the MaskFormatter to the JFormattedTextField
            birthday.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(dateFormatter));
            
            
        } catch (ParseException e) {
            e.printStackTrace();
        }
		
		// Telephone Number label
		JLabel lblNewLabel_1_1_1_1 = new JLabel("Telephone No.");
		lblNewLabel_1_1_1_1.setBounds(31, 456, 158, 31);
		lblNewLabel_1_1_1_1.setForeground(new Color(0, 0, 0));
        lblNewLabel_1_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
        
        contentPane.add(lblNewLabel_1_1_1_1);
        // Telephone Number formatted text field
        try {
            // Create a MaskFormatter for the phone number format
            MaskFormatter phoneFormatter = new MaskFormatter("+44"+"##########");
            phoneFormatter.setPlaceholderCharacter('_'); // Optional: Set a placeholder character

            // Create a JFormattedTextField with the specified MaskFormatter
            phoneno = new JFormattedTextField(phoneFormatter);
            phoneno.setBorder(null);
            phoneno.setBounds(189, 456, 354, 31);
            phoneno.setFont(new Font("Tahoma", Font.PLAIN, 20));
            phoneno.setBackground(new Color(255, 255, 255));
            phoneno.setForeground(new Color(0, 0, 0));
            contentPane.add(phoneno);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        	
            
            JLabel lblNewLabel_1_1_1_1_1_1 = new JLabel("House No.");
            lblNewLabel_1_1_1_1_1_1.setBounds(31, 512, 158, 31);
            lblNewLabel_1_1_1_1_1_1.setForeground(new Color(0, 0, 0));
            lblNewLabel_1_1_1_1_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
            contentPane.add(lblNewLabel_1_1_1_1_1_1);
            
            HouseNumber = new JTextField();
            HouseNumber.setBorder(null);
            HouseNumber.setBounds(189, 512, 354, 31);
            HouseNumber.setFont(new Font("Tahoma", Font.PLAIN, 20));
            HouseNumber.setForeground(new Color(0, 0, 0));
            HouseNumber.setBackground(new Color(255, 255, 255));
            HouseNumber.setColumns(10);
            contentPane.add(HouseNumber);
            
            JLabel lblNewLabel_1_1_1_1_1_2 = new JLabel("Street Name");
            lblNewLabel_1_1_1_1_1_2.setBounds(603, 512, 158, 31);
            lblNewLabel_1_1_1_1_1_2.setForeground(new Color(0, 0, 0));
            lblNewLabel_1_1_1_1_1_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
            contentPane.add(lblNewLabel_1_1_1_1_1_2);
            
            StreetName = new JTextField();
            StreetName.setBorder(null);
            StreetName.setBounds(771, 512, 344, 31);
            StreetName.setFont(new Font("Tahoma", Font.PLAIN, 20));
            StreetName.setForeground(new Color(0, 0, 0));
            StreetName.setBackground(new Color(255, 255, 255));
            StreetName.setColumns(10);
            contentPane.add(StreetName);
            
            JLabel lblNewLabel_1_1_1_1_1_2_1 = new JLabel("City or Town");
            lblNewLabel_1_1_1_1_1_2_1.setBounds(31, 564, 158, 31);
            lblNewLabel_1_1_1_1_1_2_1.setForeground(new Color(0, 0, 0));
            lblNewLabel_1_1_1_1_1_2_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
            contentPane.add(lblNewLabel_1_1_1_1_1_2_1);
            
            CityorTown = new JTextField();
            CityorTown.setBorder(null);
            CityorTown.setBounds(189, 564, 354, 31);
            CityorTown.setFont(new Font("Tahoma", Font.PLAIN, 20));
            CityorTown.setForeground(new Color(0, 0, 0));
            CityorTown.setBackground(new Color(255, 255, 255));
            CityorTown.setColumns(10);
            contentPane.add(CityorTown);
            
            JLabel lblNewLabel_1_1_1_1_1_2_1_1 = new JLabel("Postal Code");
            lblNewLabel_1_1_1_1_1_2_1_1.setBounds(603, 564, 158, 31);
            lblNewLabel_1_1_1_1_1_2_1_1.setForeground(new Color(0, 0, 0));
            lblNewLabel_1_1_1_1_1_2_1_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
            contentPane.add(lblNewLabel_1_1_1_1_1_2_1_1);
            
            postalcode = new JTextField();
            postalcode.setBorder(null);
            postalcode.setBounds(771, 564, 344, 31);
            postalcode.setFont(new Font("Tahoma", Font.PLAIN, 20));
            postalcode.setForeground(new Color(0, 0, 0));
            postalcode.setBackground(new Color(255, 255, 255));
            postalcode.setColumns(10);
            contentPane.add(postalcode);
            
         // Medical Problem Combo Box
            medicalproblem = new JComboBox<>();
            medicalproblem.setBackground(new Color(255, 255, 255));
            medicalproblem.setBounds(31, 98, 502, 34);
            contentPane.add(medicalproblem);
            // Call the method to connect and populate the JComboBox
            connectAndPopulateComboBox(medicalproblem);
            
            JLabel lblNewLabel_2 = new JLabel("Select Your Medical Problem");
            lblNewLabel_2.setBounds(31, 49, 329, 39);
            lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
            lblNewLabel_2.setBackground(new Color(0, 0, 0));
            lblNewLabel_2.setForeground(new Color(0, 0, 0));
            contentPane.add(lblNewLabel_2);
            
            JButton newmedical = new JButton("Add new Medical Problem");
            newmedical.setBounds(578, 98, 282, 34);
            newmedical.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            	Newmedical newm=new Newmedical();
            	newm.setVisible(true);
            	}
            });
            newmedical.setBorder(new LineBorder(new Color(0, 0, 0), 3));
            newmedical.setForeground(new Color(0, 0, 0));
            newmedical.setBackground(new Color(255, 255, 255));
            newmedical.setFont(new Font("Tahoma", Font.PLAIN, 20));
            contentPane.add(newmedical);
            
         // Email ID label and text field
            JLabel lblNewLabel_1_1_2 = new JLabel("Email ID");
            lblNewLabel_1_1_2.setForeground(new Color(0, 0, 0));
            lblNewLabel_1_1_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
            lblNewLabel_1_1_2.setBounds(31, 347, 158, 31);
            contentPane.add(lblNewLabel_1_1_2);
            
            Email = new JTextField();
            Email.setBorder(null);
            Email.setForeground(new Color(0, 0, 0));
            Email.setFont(new Font("Tahoma", Font.PLAIN, 20));
            Email.setColumns(10);
            Email.setBackground(new Color(255, 255, 255));
            Email.setBounds(189, 347, 354, 31);
            contentPane.add(Email);
            
           
            
         // Disability label and combo box
            txtDisability = new JTextField();
            txtDisability.setBorder(null);
            txtDisability.setBackground(new Color(0, 255, 255));
            txtDisability.setForeground(new Color(0, 0, 0));
            txtDisability.setFont(new Font("Tahoma", Font.PLAIN, 20));
            txtDisability.setText(" Disability");
            txtDisability.setBounds(21, 162, 109, 43);
            contentPane.add(txtDisability);
            txtDisability.setColumns(10);
            
            Disability = new JComboBox();
            Disability.setFont(new Font("Tahoma", Font.PLAIN, 15));
            Disability.setBounds(189, 169, 354, 36);
            contentPane.add(Disability);
         // After adding comboBox_1 to your frame, add the three options to it
            Disability.addItem("No");
            Disability.addItem("Vision Impairment");
            Disability.addItem("Deaf or hard of hearing");
            Disability.addItem("Mental health conditions");
            Disability.addItem("Intellectual disability");
            Disability.addItem("Acquired brain injury");
            Disability.addItem("Autism spectrum disorder");
            Disability.addItem("Wheelchair user");
            
         // Submit button
           	JButton submit = new JButton("submit");
            submit.setBounds(497, 616, 152, 41);
            submit.setBorder(new LineBorder(new Color(0, 0, 0), 3));
            submit.setBackground(new Color(255, 255, 255));
            submit.setForeground(new Color(0, 0, 0));
            submit.setFont(new Font("Tahoma", Font.PLAIN, 20));
            submit.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		
            		// Validate first name (no integers allowed)
                    String firstName = firstname.getText().trim();
                    if (containsDigit(firstName)) {
                        showError("First name cannot contain numbers");
                        return;
                    }
                    if (firstName.isEmpty()) {
                        showError("First name cannot be empty");
                        return;
                    }
                 // Check if a value is selected in comboBox_1
                    if (Disability.getSelectedItem() == null) {
                        // Display an error message
                        JOptionPane.showMessageDialog(contentPane, "Please select a value from Diability.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; // Stop further execution
                    }
                 // Check if a value is selected in comboBox_1
                    if (title1.getSelectedItem() == null) {
                        // Display an error message
                        JOptionPane.showMessageDialog(contentPane, "Please select a value from Titile.", "Error", JOptionPane.ERROR_MESSAGE);
                        return; // Stop further execution
                    }
                    
                    // validate the email
                    String email = Email.getText().trim(); //trim() method is used to remove any leading and trailing whitespace characters from a string.
                    if (email.isEmpty()) {
                        showError("Email cannot be empty");
                        return;
                    }

                    // Regular expression to check email format
                    String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

                    // Compile the regex pattern
                    Pattern pattern = Pattern.compile(emailRegex);

                    // Match the email against the pattern
                    Matcher matcher = pattern.matcher(email);

                    if (!matcher.matches()) {
                        showError("Invalid email format");
                        return;
                    }

                    // Validate last name (no integers allowed)
                    String lastName = lastname.getText().trim();
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
                    String phoneNumber = phoneno.getText().trim();
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
                    setVisible(false);
                    if (storeUserData()) {
                        JOptionPane.showMessageDialog(contentPane, "Your data is stored successfully!");
                    } else {
                        JOptionPane.showMessageDialog(contentPane, "Failed to store data.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            	}
            );
            contentPane.add(submit);
            

      
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
                    
                    
                    // Insert data into the patients table
                    String insertPatientQuery = "INSERT INTO patients (visiting_number,medicalproblem,title,Disability, first_name, last_name,Email_ID, dob, phone_number, "
                            + "address, city, postal_code) VALUES (?,?,?,?, ?, ?, ?, ?, ?, ?, ?,?)";

                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertPatientQuery)) {
                        String selectedMedicalProblem = this.medicalproblem.getSelectedItem().toString();
                    	
                        String title = title1.getSelectedItem().toString();
                        String selectedDisability=Disability.getSelectedItem().toString();                        
                        String firstName = firstname.getText().trim();
                        String lastName = lastname.getText().trim();
                        String EmailID = Email.getText().trim();
                        String dob = birthday.getText().trim();
                        String phoneNumber = phoneno.getText().trim();
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
}
            }
