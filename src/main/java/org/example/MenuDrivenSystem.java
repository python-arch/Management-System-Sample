package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class MenuDrivenSystem extends JFrame {
    protected ArrayList<Student> students = new ArrayList<>();
    protected JList<String> studentList;
    protected DefaultListModel<String> listModel;
    protected JTextField nameField;
    protected JTextField gradeField;
    protected Connection connection;
    protected final String databaseUrl = "jdbc:mysql://localhost:3306/school";
    protected final String username = "root";
    protected final String password = "python8723";


    public MenuDrivenSystem() {
        setTitle("School Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        listModel = new DefaultListModel<>();
        studentList = new JList<>(listModel);
        studentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = studentList.getSelectedIndex();
                if (selectedIndex != -1) {
                    Student selectedStudent = students.get(selectedIndex);
                    nameField.setText(selectedStudent.getName());
                    gradeField.setText(selectedStudent.getGrade());
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(studentList);

        JPanel panel = new JPanel(new GridLayout(0, 2));
        nameField = new JTextField();
        gradeField = new JTextField();
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String name = nameField.getText();
            String grade = gradeField.getText();
            if (!name.isEmpty() && !grade.isEmpty()) {
                Student student = new Student(name, grade);
                try {
                    addStudentToDatabase(student);
                    students.add(student);
                    listModel.addElement(student.toString());
                    nameField.setText("");
                    gradeField.setText("");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to add student to database.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please enter both name and grade.");
            }
        });
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Grade:"));
        panel.add(gradeField);
        panel.add(new JLabel(""));
        panel.add(addButton);

        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            int selectedIndex = studentList.getSelectedIndex();
            if (selectedIndex != -1) {
                String newName = nameField.getText();
                String newGrade = gradeField.getText();
                if (!newName.isEmpty() && !newGrade.isEmpty()) {
                    Student selectedStudent = students.get(selectedIndex);
                    selectedStudent.setName(newName);
                    selectedStudent.setGrade(newGrade);
                    try {
                        updateStudentInDatabase(selectedIndex, selectedStudent);
                        listModel.set(selectedIndex, selectedStudent.toString());
                        nameField.setText("");
                        gradeField.setText("");
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Failed to update student in database.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter both name and grade.");
                }
            }
        });
        panel.add(updateButton);

        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            int selectedIndex = studentList.getSelectedIndex();
            if (selectedIndex != -1) {
                try {
                    deleteStudentFromDatabase(selectedIndex);
                    students.remove(selectedIndex);
                    listModel.remove(selectedIndex);
                    nameField.setText("");
                    gradeField.setText("");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to delete student from database.");
                }
            }
        });
        panel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // Create File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem primaryStudentMenuItem = new JMenuItem("Primary Student Window");
        primaryStudentMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrimaryStudentWindow primaryStudentWindow = new PrimaryStudentWindow();
                primaryStudentWindow.setVisible(true);
            }
        });


        fileMenu.add(primaryStudentMenuItem);


        // Create Exit menu item
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        });
        fileMenu.add(exitMenuItem);

        // Create Edit menu
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);

        // Create Display menu item
        JMenuItem displayMenuItem = new JMenuItem("Display");
        displayMenuItem.addActionListener(e -> {
            StringBuilder display = new StringBuilder();
            for (Student student : students) {
                display.append(student.toString()).append("\n");
            }
            JOptionPane.showMessageDialog(null, display.toString(), "Student List", JOptionPane.INFORMATION_MESSAGE);
        });
        editMenu.add(displayMenuItem);
        // Inside the MenuDrivenSystem class constructor after creating the 'editMenu' JMenu
        JMenuItem addMenuItem = new JMenuItem("Add");
        addMenuItem.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Enter student name:");
            String grade = JOptionPane.showInputDialog("Enter student grade:");
            if (name != null && grade != null && !name.isEmpty() && !grade.isEmpty()) {
                Student student = new Student(name, grade);
                try {
                    addStudentToDatabase(student);
                    students.add(student);
                    listModel.addElement(student.toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Failed to add student to database.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Please enter both name and grade.");
            }
        });
        editMenu.add(addMenuItem);

        JMenuItem updateMenuItem = new JMenuItem("Update");
        updateMenuItem.addActionListener(e -> {
            int selectedIndex = studentList.getSelectedIndex();
            if (selectedIndex != -1) {
                String newName = JOptionPane.showInputDialog("Enter new name:");
                String newGrade = JOptionPane.showInputDialog("Enter new grade:");
                if (newName != null && newGrade != null && !newName.isEmpty() && !newGrade.isEmpty()) {
                    Student selectedStudent = students.get(selectedIndex);
                    selectedStudent.setName(newName);
                    selectedStudent.setGrade(newGrade);
                    try {
                        updateStudentInDatabase(selectedIndex, selectedStudent);
                        listModel.set(selectedIndex, selectedStudent.toString());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Failed to update student in database.");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter both name and grade.");
                }
            }
        });
        editMenu.add(updateMenuItem);

        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.addActionListener(e -> {
            int selectedIndex = studentList.getSelectedIndex();
            if (selectedIndex != -1) {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this student?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        deleteStudentFromDatabase(selectedIndex);
                        students.remove(selectedIndex);
                        listModel.remove(selectedIndex);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Failed to delete student from database.");
                    }
                }
            }
        });
        editMenu.add(deleteMenuItem);

        // Connect to the database
        try {
            connection = DriverManager.getConnection(databaseUrl , username ,password);

            // Create students table if not exists
            String createTableSQL = "CREATE TABLE IF NOT EXISTS students (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), grade VARCHAR(10))";
            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableSQL);
            }

            // Load students from the database
            loadStudentsFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
        }
    }

    private void loadStudentsFromDatabase() throws SQLException {
        String query = "SELECT name, grade FROM students";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String grade = resultSet.getString("grade");
                Student student = new Student(name, grade);
                students.add(student);
                listModel.addElement(student.toString());
            }
        }
    }

    private void addStudentToDatabase(Student student) throws SQLException {
        String insertQuery = "INSERT INTO students (name, grade) VALUES (?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getGrade());
            preparedStatement.executeUpdate();
        }
    }

    private void updateStudentInDatabase(int index, Student student) throws SQLException {
        String updateQuery = "UPDATE students SET name=?, grade=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getGrade());
            preparedStatement.setInt(3, index + 1); // SQLite uses 1-based indexing
            preparedStatement.executeUpdate();
        }
    }

    private void deleteStudentFromDatabase(int index) throws SQLException {
        String deleteQuery = "DELETE FROM students WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, index + 1); // SQLite uses 1-based indexing
            preparedStatement.executeUpdate();
        }
    }
}
