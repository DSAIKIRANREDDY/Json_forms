import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentEnrollmentForm extends JFrame implements ActionListener {
    // Form Components
    JTextField rollNoField, fullNameField, classField, birthDateField, addressField, enrollmentDateField;
    JButton saveButton, updateButton, resetButton;

    Connection conn;
    PreparedStatement ps;
    ResultSet rs;

    // Constructor to set up the form
    public StudentEnrollmentForm() {
        // Set title and size
        setTitle("Student Enrollment Form");
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Create a panel for the form fields
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10)); // 7 rows, 2 columns, with gaps between

        // Roll No field
        formPanel.add(new JLabel("Roll No:"));
        rollNoField = new JTextField();
        formPanel.add(rollNoField);

        // Full Name field
        formPanel.add(new JLabel("Full Name:"));
        fullNameField = new JTextField();
        formPanel.add(fullNameField);

        // Class field
        formPanel.add(new JLabel("Class:"));
        classField = new JTextField();
        formPanel.add(classField);

        // Birth Date field
        formPanel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        birthDateField = new JTextField();
        formPanel.add(birthDateField);

        // Address field
        formPanel.add(new JLabel("Address:"));
        addressField = new JTextField();
        formPanel.add(addressField);

        // Enrollment Date field
        formPanel.add(new JLabel("Enrollment Date (YYYY-MM-DD):"));
        enrollmentDateField = new JTextField();
        formPanel.add(enrollmentDateField);

        // Add the form panel to the frame
        add(formPanel, BorderLayout.CENTER);

        // Create a panel for the buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Save button
        saveButton = new JButton("Save");
        buttonPanel.add(saveButton);

        // Update button
        updateButton = new JButton("Update");
        updateButton.setEnabled(false);  // Disabled initially
        buttonPanel.add(updateButton);

        // Reset button
        resetButton = new JButton("Reset");
        buttonPanel.add(resetButton);

        // Add the button panel to the frame
        add(buttonPanel, BorderLayout.SOUTH);

        // Action Listeners
        rollNoField.addActionListener(this);
        saveButton.addActionListener(this);
        updateButton.addActionListener(this);
        resetButton.addActionListener(this);

        // Set up the Database connection
        setUpDatabase();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    // Setting up the Database connection
    private void setUpDatabase() {
        try {
            // Load the JDBC driver (for MySQL)
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/SCHOOL_DB", "root", "password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Action handling for buttons and form interactions
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == rollNoField) {
            String rollNo = rollNoField.getText();
            if (!rollNo.isEmpty()) {
                checkRollNo(rollNo);
            }
        } else if (e.getSource() == saveButton) {
            saveData();
        } else if (e.getSource() == updateButton) {
            updateData();
        } else if (e.getSource() == resetButton) {
            resetForm();
        }
    }

    // Check if the Roll No exists in the database
    private void checkRollNo(String rollNo) {
        try {
            String query = "SELECT * FROM STUDENT_TABLE WHERE Roll_No = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, rollNo);
            rs = ps.executeQuery();

            if (rs.next()) {
                // If Roll No exists, populate fields with data and enable Update
                fullNameField.setText(rs.getString("Full_Name"));
                classField.setText(rs.getString("Class"));
                birthDateField.setText(rs.getString("Birth_Date"));
                addressField.setText(rs.getString("Address"));
                enrollmentDateField.setText(rs.getString("Enrollment_Date"));

                // Enable fields for editing and the Update button
                enableFields(true);
                updateButton.setEnabled(true);
                saveButton.setEnabled(false);  // Disable save since it already exists
            } else {
                // If Roll No does not exist, enable fields for new data entry and Save
                resetForm();  // Reset the form fields first
                enableFields(true);
                saveButton.setEnabled(true);  // Enable save for new records
                updateButton.setEnabled(false);  // Disable update for new records
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Method to enable/disable form fields
    private void enableFields(boolean enable) {
        fullNameField.setEnabled(enable);
        classField.setEnabled(enable);
        birthDateField.setEnabled(enable);
        addressField.setEnabled(enable);
        enrollmentDateField.setEnabled(enable);

        // Focus on the first enabled field
        if (enable) {
            fullNameField.requestFocus(); // Focus on the Full Name field
        }
    }

    // Save data to the database
    private void saveData() {
        try {
            // Validate data entry
            if (validateFields()) {
                String query = "INSERT INTO STUDENT_TABLE (Roll_No, Full_Name, Class, Birth_Date, Address, Enrollment_Date) VALUES (?, ?, ?, ?, ?, ?)";
                ps = conn.prepareStatement(query);
                ps.setInt(1, Integer.parseInt(rollNoField.getText()));
                ps.setString(2, fullNameField.getText());
                ps.setString(3, classField.getText());
                ps.setString(4, birthDateField.getText());
                ps.setString(5, addressField.getText());
                ps.setString(6, enrollmentDateField.getText());

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Record Saved!");

                // Hide the form after saving the record
                this.setVisible(false);
                // Optionally dispose of the frame to free resources
                this.dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Update data in the database
    private void updateData() {
        try {
            // Validate data entry
            if (validateFields()) {
                String query = "UPDATE STUDENT_TABLE SET Full_Name=?, Class=?, Birth_Date=?, Address=?, Enrollment_Date=? WHERE Roll_No=?";
                ps = conn.prepareStatement(query);
                ps.setString(1, fullNameField.getText());
                ps.setString(2, classField.getText());
                ps.setString(3, birthDateField.getText());
                ps.setString(4, addressField.getText());
                ps.setString(5, enrollmentDateField.getText());
                ps.setInt(6, Integer.parseInt(rollNoField.getText()));

                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Record Updated!");
                
                // Hide the form after updating the record
                this.setVisible(false);
                // Optionally dispose of the frame to free resources
                this.dispose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Validate the form fields
    private boolean validateFields() {
        if (rollNoField.getText().isEmpty() || fullNameField.getText().isEmpty() ||
            classField.getText().isEmpty() || birthDateField.getText().isEmpty() ||
            addressField.getText().isEmpty() || enrollmentDateField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled!");
            return false;
        }
        return true;
    }

    // Reset the form
    private void resetForm() {
        rollNoField.setText("");
        fullNameField.setText("");
        classField.setText("");
        birthDateField.setText("");
        addressField.setText("");
        enrollmentDateField.setText("");

        enableFields(false); // Disable all fields

        saveButton.setEnabled(false);
        updateButton.setEnabled(false);
        rollNoField.requestFocus();  // Set focus back to Roll No field
    }

    public static void main(String[] args) {
        new StudentEnrollmentForm();
    }
}
