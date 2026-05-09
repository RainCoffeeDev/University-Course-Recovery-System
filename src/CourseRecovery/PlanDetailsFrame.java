package CourseRecovery;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class PlanDetailsFrame extends JFrame {
    
    private String studentID;
    private String courseID;
    private JTable taskTable;
    private DefaultTableModel model;
    private String filename = "recovery_plans.csv";
    private JLabel lblStatus; // To show if Active or Completed

    public PlanDetailsFrame(String studentID, String courseID) {
        this.studentID = studentID;
        this.courseID = courseID;
        
        setTitle("Recovery Plan: " + studentID + " - " + courseID);
        setSize(650, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1. TOP: Info Label & Status
        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        JLabel lblInfo = new JLabel("  Managing Recovery Tasks for: " + studentID + " (" + courseID + ")");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 14));
        
        lblStatus = new JLabel("  Current Status: Loading...");
        lblStatus.setForeground(Color.BLUE);
        
        topPanel.add(lblInfo);
        topPanel.add(lblStatus);
        add(topPanel, BorderLayout.NORTH);

        // 2. CENTER: The Table
        String[] cols = {"Week", "Task / Milestone"};
        model = new DefaultTableModel(cols, 0);
        taskTable = new JTable(model);
        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        // 3. BOTTOM: Inputs and Buttons
        JPanel bottomPanel = new JPanel(new GridLayout(3, 1)); 

        // Row 1: Inputs
        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("Week:"));
        JTextField txtWeek = new JTextField(8);
        inputPanel.add(txtWeek);
        inputPanel.add(new JLabel("Task:"));
        JTextField txtTask = new JTextField(25);
        inputPanel.add(txtTask);
        bottomPanel.add(inputPanel);

        // Row 2: Task Buttons (Add, Update, Delete)
        JPanel btnPanel = new JPanel();
        JButton btnAdd = new JButton("Add Task");
        JButton btnUpdate = new JButton("Update Selected Task"); // NEW
        JButton btnDelete = new JButton("Delete Selected Task");
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        bottomPanel.add(btnPanel);

        // Row 3: Plan Status Button
        JPanel statusPanel = new JPanel();
        JButton btnComplete = new JButton("Mark Plan as COMPLETED"); // NEW
        btnComplete.setBackground(Color.GREEN);
        statusPanel.add(btnComplete);
        bottomPanel.add(statusPanel);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- MOUSE CLICK: Fill text boxes when row clicked ---
        taskTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = taskTable.getSelectedRow();
                if (row != -1) {
                    txtWeek.setText((String) model.getValueAt(row, 0));
                    txtTask.setText((String) model.getValueAt(row, 1));
                }
            }
        });

        // --- BUTTON LOGIC ---

        // 1. ADD
        btnAdd.addActionListener(e -> {
            String w = txtWeek.getText();
            String t = txtTask.getText();
            if(!w.isEmpty() && !t.isEmpty()) {
                saveTask(w, t);
                model.addRow(new Object[]{w, t});
                txtWeek.setText("");
                txtTask.setText("");
            }
        });

        // 2. UPDATE (Requirements: Add/Update/Remove)
        btnUpdate.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a task to update.");
                return;
            }
            // Get old values
            String oldWeek = (String) model.getValueAt(row, 0);
            String oldTask = (String) model.getValueAt(row, 1);
            
            // Get new values
            String newWeek = txtWeek.getText();
            String newTask = txtTask.getText();

            if(!newWeek.isEmpty() && !newTask.isEmpty()) {
                updateTaskInFile(oldWeek, oldTask, newWeek, newTask);
                // Update table visual
                model.setValueAt(newWeek, row, 0);
                model.setValueAt(newTask, row, 1);
                JOptionPane.showMessageDialog(this, "Task Updated!");
            }
        });

        // 3. DELETE
        btnDelete.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row != -1) {
                String w = (String) model.getValueAt(row, 0);
                String t = (String) model.getValueAt(row, 1);
                deleteTaskFromFile(w, t);
                model.removeRow(row);
                txtWeek.setText("");
                txtTask.setText("");
            }
        });

        // 4. MARK COMPLETED
        btnComplete.addActionListener(e -> {
            markPlanCompleted();
            lblStatus.setText("  Current Status: Completed");
            JOptionPane.showMessageDialog(this, "Plan marked as Completed!");
        });

        loadTasks(); 
        setVisible(true);
    }

    // --- FILE OPERATIONS ---

    private void loadTasks() {
        String status = "Active"; // Default
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    if (data[0].equals(studentID) && data[1].equals(courseID)) {
                        model.addRow(new Object[]{data[3], data[4]});
                        status = data[2]; // Capture the status
                    }
                }
            }
            lblStatus.setText("  Current Status: " + status);
        } catch (Exception e) {}
    }

    private void saveTask(String week, String task) {
        try (FileWriter fw = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(studentID + "," + courseID + ",Active," + week + "," + task);
        } catch (IOException e) {}
    }

    private void deleteTaskFromFile(String targetWeek, String targetTask) {
        processFile(targetWeek, targetTask, null, null, "DELETE");
    }

    private void updateTaskInFile(String oldWeek, String oldTask, String newWeek, String newTask) {
        processFile(oldWeek, oldTask, newWeek, newTask, "UPDATE");
    }
    
    private void markPlanCompleted() {
        processFile(null, null, null, null, "COMPLETE_ALL");
    }

    // MASTER METHOD to handle Read/Write logic for CSV
    private void processFile(String targetWeek, String targetTask, String newWeek, String newTask, String mode) {
        ArrayList<String> lines = new ArrayList<>();
        File file = new File(filename);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    boolean isMyStudent = data[0].equals(studentID) && data[1].equals(courseID);
                    
                    if (mode.equals("COMPLETE_ALL") && isMyStudent) {
                        // Change status to Completed for ALL rows of this student
                        lines.add(data[0] + "," + data[1] + ",Completed," + data[3] + "," + data[4]);
                    } 
                    else if (isMyStudent && data[3].equals(targetWeek) && data[4].equals(targetTask)) {
                        // Found the specific row
                        if (mode.equals("DELETE")) {
                            continue; // Skip adding this line (Delete it)
                        } else if (mode.equals("UPDATE")) {
                            // Replace with new values
                            lines.add(data[0] + "," + data[1] + "," + data[2] + "," + newWeek + "," + newTask);
                        } else {
                            lines.add(line);
                        }
                    } else {
                        lines.add(line); // Keep other lines as is
                    }
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }

        // Overwrite file
        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String l : lines) pw.println(l);
        } catch (IOException e) { e.printStackTrace(); }
    }
}