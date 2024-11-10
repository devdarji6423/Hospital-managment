package patientinfo;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;

import patientinfo.Doctor.ImagePanel;

import javax.swing.JLabel;

public class Home extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Home frame = new Home();
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
	public Home() {
		// Create content pane with background image
		contentPane = new ImagePanel("D:\\Brunel\\group project all documnent\\3.jpg"); // Specify the path to your image
		setContentPane(contentPane);  // Set the content pane here
		contentPane.setLayout(null);
		
		// Button for signing out
		JButton btnNewButton = new JButton("Sign out");
		btnNewButton.setBorder(new LineBorder(new Color(255, 255, 255), 2, true));
		btnNewButton.setFocusPainted(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();  // Close the current frame
			}
		});
		btnNewButton.setContentAreaFilled(false);
		btnNewButton.setForeground(new Color(255, 255, 255));
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnNewButton.setBackground(new Color(51, 51, 102));
		btnNewButton.setBounds(987, 22, 173, 40);
		contentPane.add(btnNewButton);
		
		// Button for patient information
		JButton btnNewButton_1 = new JButton("");
		btnNewButton_1.setBorder(new LineBorder(new Color(0, 0, 0), 5));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Patientinformation patientinfo=new Patientinformation();
            	patientinfo.setVisible(true);  // Open Patientinformation frame
			}
		});
		btnNewButton_1.setIcon(new ImageIcon("C:\\Users\\DEV\\Downloads\\medical.png"));
		btnNewButton_1.setBounds(36, 211, 312, 286);
		contentPane.add(btnNewButton_1);
		
		// Button for kin information
		JButton btnNewButton_2 = new JButton("");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				KinInformation kininfo=new KinInformation();
				kininfo.setVisible(true);  // Open KinInformation frame
			}
		});
		btnNewButton_2.setBorder(new LineBorder(new Color(0, 0, 0), 5));
		btnNewButton_2.setIcon(new ImageIcon("C:\\Users\\DEV\\Downloads\\family (4).png"));
		btnNewButton_2.setBounds(440, 211, 318, 286);
		contentPane.add(btnNewButton_2);
		
		// Button for previous records
		JButton btnNewButton_3 = new JButton("");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Previousrecord prev=new Previousrecord();
				prev.setVisible(true);  // Open Previousrecord frame
			}
		});
		btnNewButton_3.setBorder(new LineBorder(new Color(0, 0, 0), 5));
		btnNewButton_3.setIcon(new ImageIcon("C:\\Users\\DEV\\Downloads\\folder.png"));
		btnNewButton_3.setBounds(827, 211, 318, 286);
		contentPane.add(btnNewButton_3);
		
		// Label for hospital name
		JLabel lblNewLabel = new JLabel("Brunel Multi-Speciality Hospital");
		lblNewLabel.setForeground(Color.CYAN);
		lblNewLabel.setFont(new Font("Arial", Font.BOLD, 40));
		lblNewLabel.setBounds(249, 22, 680, 135);
		contentPane.add(lblNewLabel);
		
		// Labels for button descriptions
		JLabel lblNewLabel_1 = new JLabel("Patient Information");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_1.setForeground(new Color(255, 255, 255));
		lblNewLabel_1.setBounds(103, 524, 181, 34);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Next Kin Information");
		lblNewLabel_2.setForeground(new Color(255, 255, 255));
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_2.setBounds(502, 524, 196, 34);
		contentPane.add(lblNewLabel_2);
		
		JLabel lblNewLabel_3 = new JLabel("Previous Record");
		lblNewLabel_3.setForeground(new Color(255, 255, 255));
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblNewLabel_3.setBounds(934, 524, 143, 34);
		contentPane.add(lblNewLabel_3);
		
		// Set frame properties
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1184, 718);	
	}
}
