package com.example.sms.ui;

import com.example.sms.dao.StudentDAO;
import com.example.sms.model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class MainFrame extends JFrame {
    private JTextField txtName, txtEmail, txtCourse, txtSearch;
    private JButton btnAdd, btnUpdate, btnDelete, btnSearch;
    private JTable table;
    private DefaultTableModel tableModel;
    private StudentDAO studentDAO = new StudentDAO();
    private int selectedStudentId = -1;

    public MainFrame() {
        setTitle("Student Management System");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadStudents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel panelTop = new JPanel(new GridLayout(2, 4, 10, 10));
        txtName = new JTextField();
        txtEmail = new JTextField();
        txtCourse = new JTextField();
        txtSearch = new JTextField();

        panelTop.add(new JLabel("Name"));
        panelTop.add(new JLabel("Email"));
        panelTop.add(new JLabel("Course"));
        panelTop.add(new JLabel("Search"));

        panelTop.add(txtName);
        panelTop.add(txtEmail);
        panelTop.add(txtCourse);
        panelTop.add(txtSearch);

        add(panelTop, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[] { "ID", "Name", "Email", "Course" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel panelBottom = new JPanel();
        btnAdd = new JButton("Add");
        btnUpdate = new JButton("Update");
        btnDelete = new JButton("Delete");
        btnSearch = new JButton("Search");

        panelBottom.add(btnAdd);
        panelBottom.add(btnUpdate);
        panelBottom.add(btnDelete);
        panelBottom.add(btnSearch);

        add(panelBottom, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnSearch.addActionListener(e -> searchStudents());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedStudentId = (int) tableModel.getValueAt(row, 0);
                    txtName.setText((String) tableModel.getValueAt(row, 1));
                    txtEmail.setText((String) tableModel.getValueAt(row, 2));
                    txtCourse.setText((String) tableModel.getValueAt(row, 3));
                }
            }
        });
    }

    private void loadStudents() {
        try {
            List<Student> students = studentDAO.getAllStudents();
            refreshTable(students);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void refreshTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student s : students) {
            tableModel.addRow(new Object[] { s.getId(), s.getName(), s.getEmail(), s.getCourse() });
        }
    }

    private void addStudent() {
        try {
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String course = txtCourse.getText().trim();
            if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            studentDAO.addStudent(new Student(name, email, course));
            JOptionPane.showMessageDialog(this, "Student added!");
            clearFields();
            loadStudents();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void updateStudent() {
        if (selectedStudentId == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to update", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String course = txtCourse.getText().trim();
            if (name.isEmpty() || email.isEmpty() || course.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            studentDAO.updateStudent(new Student(selectedStudentId, name, email, course));
            JOptionPane.showMessageDialog(this, "Student updated!");
            clearFields();
            loadStudents();
            selectedStudentId = -1;
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void deleteStudent() {
        if (selectedStudentId == -1) {
            JOptionPane.showMessageDialog(this, "Select a student to delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                studentDAO.deleteStudent(selectedStudentId);
                JOptionPane.showMessageDialog(this, "Student deleted!");
                clearFields();
                loadStudents();
                selectedStudentId = -1;
            } catch (SQLException ex) {
                showError(ex);
            }
        }
    }

    private void searchStudents() {
        String keyword = txtSearch.getText().trim();
        try {
            List<Student> students;
            if (keyword.isEmpty()) {
                students = studentDAO.getAllStudents();
            } else {
                students = studentDAO.searchStudents(keyword);
            }
            refreshTable(students);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtEmail.setText("");
        txtCourse.setText("");
        txtSearch.setText("");
        selectedStudentId = -1;
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
