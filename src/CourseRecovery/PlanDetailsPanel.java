package CourseRecovery;

import MainMenu.MainMenuFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.io.*;

import MainMenu.MainMenuFrame.RoundedHoverButton;

public class PlanDetailsPanel extends JPanel {

    private final MainMenuFrame parent;

    private JTable taskTable;
    private DefaultTableModel model;
    private JLabel lblInfo, lblStatus;
    private JTextField txtWeek, txtTask;
    private String currentStudentID, currentCourseID;
    private final String filename = "recovery_plans.csv";

    public PlanDetailsPanel(MainMenuFrame parent) {
        this.parent = parent;

        setOpaque(false);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        RoundedHoverButton btnBack = new RoundedHoverButton("Back");
        btnBack.setPreferredSize(new Dimension(80, 30));
        btnBack.addActionListener(e -> parent.showCard(MainMenuFrame.CARD_RECOVERY));

        JPanel titleBox = new JPanel(new GridLayout(2, 1));
        titleBox.setOpaque(false);

        lblInfo = new JLabel("Managing Tasks");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInfo.setForeground(Color.WHITE);

        lblStatus = new JLabel("Status: Loading...");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setForeground(new Color(0, 255, 127));

        titleBox.add(lblInfo);
        titleBox.add(lblStatus);
        titleBox.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));

        topPanel.add(btnBack, BorderLayout.WEST);
        topPanel.add(titleBox, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"Week", "Task / Milestone"}, 0);
        taskTable = new JTable(model);

        taskTable.setBackground(new Color(30, 30, 50));
        taskTable.setForeground(Color.WHITE);
        taskTable.setGridColor(new Color(60, 60, 80));
        taskTable.setRowHeight(25);
        taskTable.getTableHeader().setBackground(new Color(10, 20, 40));
        taskTable.getTableHeader().setForeground(Color.WHITE);

        taskTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = taskTable.getSelectedRow();
                if (row != -1) {
                    txtWeek.setText((String) model.getValueAt(row, 0));
                    txtTask.setText((String) model.getValueAt(row, 1));
                }
            }
        });

        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createTitledBorder(
                null, "Manage Tasks", 0, 0, null, Color.WHITE));

        JPanel inputGrid = new JPanel(new GridLayout(2, 2, 5, 5));
        inputGrid.setOpaque(false);

        JLabel l1 = new JLabel("Week:");
        l1.setForeground(Color.WHITE);
        JLabel l2 = new JLabel("Task:");
        l2.setForeground(Color.WHITE);
        txtWeek = new JTextField();
        txtTask = new JTextField();

        inputGrid.add(l1);
        inputGrid.add(txtWeek);
        inputGrid.add(l2);
        inputGrid.add(txtTask);
        bottomPanel.add(inputGrid, BorderLayout.CENTER);

        JPanel btnGrid = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnGrid.setOpaque(false);

        RoundedHoverButton btnAdd      = new RoundedHoverButton("Add");
        RoundedHoverButton btnUpdate   = new RoundedHoverButton("Update");
        RoundedHoverButton btnDelete   = new RoundedHoverButton("Delete");
        RoundedHoverButton btnComplete = new RoundedHoverButton("Mark Completed");
        btnComplete.setBackground(new Color(0, 100, 0));

        btnAdd.addActionListener(e -> addTask());
        btnUpdate.addActionListener(e -> updateTask());
        btnDelete.addActionListener(e -> deleteTask());
        btnComplete.addActionListener(e -> markCompleted());

        btnGrid.add(btnAdd);
        btnGrid.add(btnUpdate);
        btnGrid.add(btnDelete);
        btnGrid.add(btnComplete);
        bottomPanel.add(btnGrid, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setTarget(String sID, String cID) {
        this.currentStudentID = sID;
        this.currentCourseID = cID;
        lblInfo.setText("Tasks for: " + sID + " (" + cID + ")");
        loadTasksFromFile();
    }

    private void loadTasksFromFile() {
        model.setRowCount(0);
        String status = "Active";
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    if (data[0].equals(currentStudentID) && data[1].equals(currentCourseID)) {
                        model.addRow(new Object[]{data[3], data[4]});
                        status = data[2];
                    }
                }
            }
            lblStatus.setText("Status: " + status);
        } catch (Exception e) {
            lblStatus.setText("Status: Active");
        }
    }

    private void addTask() {
        String w = txtWeek.getText().trim();
        String t = txtTask.getText().trim();
        if (!w.isEmpty() && !t.isEmpty()) {
            saveToFile(w, t);
            model.addRow(new Object[]{w, t});
            txtWeek.setText("");
            txtTask.setText("");
        }
    }

    private void updateTask() {
        int row = taskTable.getSelectedRow();
        if (row == -1) return;

        String oldWeek = (String) model.getValueAt(row, 0);
        String oldTask = (String) model.getValueAt(row, 1);
        String newWeek = txtWeek.getText().trim();
        String newTask = txtTask.getText().trim();

        if (!newWeek.isEmpty() && !newTask.isEmpty()) {
            processFile(oldWeek, oldTask, newWeek, newTask, "UPDATE");
            model.setValueAt(newWeek, row, 0);
            model.setValueAt(newTask, row, 1);
            txtWeek.setText("");
            txtTask.setText("");
        }
    }

    private void deleteTask() {
        int row = taskTable.getSelectedRow();
        if (row != -1) {
            String w = (String) model.getValueAt(row, 0);
            String t = (String) model.getValueAt(row, 1);
            processFile(w, t, null, null, "DELETE");
            model.removeRow(row);
            txtWeek.setText("");
            txtTask.setText("");
        }
    }

    private void markCompleted() {
        processFile(null, null, null, null, "COMPLETE_ALL");
        lblStatus.setText("Status: Completed");
        JOptionPane.showMessageDialog(this, "Plan marked as Completed!");
    }

    private void saveToFile(String week, String task) {
        try (FileWriter fw = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(currentStudentID + "," + currentCourseID + ",Active," + week + "," + task);
        } catch (Exception e) {
            // ignore for assignment
        }
    }

    private void processFile(String targetWeek, String targetTask,
                             String newWeek, String newTask, String mode) {
        ArrayList<String> lines = new ArrayList<>();
        File file = new File(filename);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    boolean isMyStudent = data[0].equals(currentStudentID) && data[1].equals(currentCourseID);

                    if ("COMPLETE_ALL".equals(mode) && isMyStudent) {
                        lines.add(data[0] + "," + data[1] + ",Completed," + data[3] + "," + data[4]);
                    } else if (isMyStudent && data[3].equals(targetWeek) && data[4].equals(targetTask)) {
                        if ("DELETE".equals(mode)) {
                            continue;
                        } else if ("UPDATE".equals(mode)) {
                            lines.add(data[0] + "," + data[1] + "," + data[2] + "," + newWeek + "," + newTask);
                        } else {
                            lines.add(line);
                        }
                    } else {
                        lines.add(line);
                    }
                } else {
                    lines.add(line);
                }
            }
        } catch (Exception e) {
            // ignore
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String l : lines) pw.println(l);
        } catch (Exception e) {
            // ignore
        }
    }
}
