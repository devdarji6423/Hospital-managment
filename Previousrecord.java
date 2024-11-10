package patientinfo;
import java.awt.EventQueue;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import patientinfo.Doctor.ImagePanel;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Previousrecord extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtPreviousRecord;
    private JButton btnSearch;
    private JTable table;

    private static final String DB_URL = "jdbc:sqlite:D:\\Brunel\\group project all documnent\\patientinformationdata.db";
    private static final String DB_URL1 = "jdbc:sqlite:D:\\Brunel\\group project all documnent\\Kininforamtion.db";
    private static final String ID_NUMBER = "visiting_number";
    private JButton btnNewButton;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Previousrecord frame = new Previousrecord();
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
    public Previousrecord() {
    	contentPane = new ImagePanel("D:\\Brunel\\group project all documnent\\3.jpg"); // Specify the path to your image
		setContentPane(contentPane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1184, 718);
        contentPane.setLayout(null);

        txtPreviousRecord = new JTextField();
        txtPreviousRecord.setBounds(422, 83, 310, 47);
        txtPreviousRecord.setSelectedTextColor(new Color(255, 0, 0));
        txtPreviousRecord.setFont(new Font("Tahoma", Font.PLAIN, 25));
        contentPane.add(txtPreviousRecord);
        txtPreviousRecord.setColumns(10);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(779, 81, 170, 47);
        btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 30));
        contentPane.add(btnSearch);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0, 166, 1184, 400);
        scrollPane.setBackground(new Color(0, 0, 0));
        scrollPane.setForeground(new Color(0, 0, 0));
        scrollPane.setFont(new Font("Tahoma", Font.PLAIN, 25));
        contentPane.add(scrollPane);
        
        
        table = new JTable();
        table.setForeground(new Color(255, 255, 255));
        table.setBackground(new Color(0, 0, 0));
        scrollPane.setViewportView(table);
        
        JLabel lblNewLabel = new JLabel("Previous Record");
        lblNewLabel.setBounds(103, 83, 257, 56);
        lblNewLabel.setForeground(new Color(0, 255, 0));
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 30));
        lblNewLabel.setBackground(new Color(0, 255, 0));
        contentPane.add(lblNewLabel);
        
        btnNewButton = new JButton("Exit");
        btnNewButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		Home Home=new Home();
        		Home.setVisible(true);
        	}
        });
        btnNewButton.setBounds(502, 610, 120, 27);
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 15));
        contentPane.add(btnNewButton);

        btnSearch.addActionListener(e -> {
            String id = txtPreviousRecord.getText().trim();
            if (!id.isEmpty()) {
                try (
                    Connection connection = DriverManager.getConnection(DB_URL);
                    Connection connection1 = DriverManager.getConnection(DB_URL1);
                ) {
                    String query = "SELECT first_name, last_name, medicalproblem, Disability, Apointment_Date, Doctor_Name, Cost FROM patients WHERE visiting_number = ?";
                    String query1 = "SELECT first_name, last_name, medicalproblem, Disability, Apointment_Date, Doctor_Name, Cost FROM kininformation WHERE visiting_number = ?";
                    PreparedStatement statement = null;
                    ResultSet resultSet = null;

                    // Execute query on the first database
                    statement = connection.prepareStatement(query);
                    statement.setString(1, id);
                    resultSet = statement.executeQuery();

                    // Check if any results are obtained
                    if (resultSet.next()) {
                        // Populate table with the fetched data
                        DefaultTableModel model = new DefaultTableModel();
                        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                            model.addColumn(resultSet.getMetaData().getColumnName(i));
                        }
                        do {
                            Object[] row = new Object[resultSet.getMetaData().getColumnCount()];
                            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                                row[i - 1] = resultSet.getObject(i);
                            }
                            model.addRow(row);
                        } while (resultSet.next());
                        table.setModel(model);
                    } else {
                        // Execute the same query on the second database
                        statement = connection1.prepareStatement(query1);
                        statement.setString(1, id);
                        resultSet = statement.executeQuery();

                        // Populate table with the fetched data
                        DefaultTableModel model = new DefaultTableModel();
                        for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                            model.addColumn(resultSet.getMetaData().getColumnName(i));
                        }
                        while (resultSet.next()) {
                            Object[] row = new Object[resultSet.getMetaData().getColumnCount()];
                            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                                row[i - 1] = resultSet.getObject(i);
                            }
                            model.addRow(row);
                        }
                        table.setModel(model);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
;
    }
}
