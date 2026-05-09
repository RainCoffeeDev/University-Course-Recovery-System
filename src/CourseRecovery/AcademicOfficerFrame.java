package CourseRecovery;

import CourseRecovery.CourseRecoveryManager;
import CourseRecovery.PlanDetailsFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class AcademicOfficerFrame extends JFrame {
    
    private CourseRecoveryManager manager;
    private JTable failedTable, planTable;
    private DefaultTableModel failedModel, planModel;

    public AcademicOfficerFrame() {
        manager = new CourseRecoveryManager();
        setTitle("Academic Officer Dashboard");
        setSize(800, 600);
        // DISPOSE_ON_CLOSE ensures it doesn't close the whole app, just this window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setLayout(new GridLayout(2, 1)); 

        // TOP PANEL
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Step 1: Detected Failures (From grades.csv)"));
        failedModel = new DefaultTableModel(new String[]{"Student ID", "Course ID", "Grade"}, 0);
        failedTable = new JTable(failedModel);
        topPanel.add(new JScrollPane(failedTable), BorderLayout.CENTER);
        
        JButton btnLoad = new JButton("1. Scan for Failures");
        btnLoad.addActionListener(e -> {
            failedModel.setRowCount(0);
            ArrayList<CourseRecoveryManager.FailedStudent> list = manager.getFailedStudents();
            for (CourseRecoveryManager.FailedStudent s : list) {
                failedModel.addRow(new Object[]{s.studentID, s.courseID, s.grade});
            }
            if (list.isEmpty()) JOptionPane.showMessageDialog(null, "No failures found (or check grades.csv)!");
        });
        topPanel.add(btnLoad, BorderLayout.SOUTH);
        add(topPanel);

        // --- BOTTOM PANEL: Generated Plans ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Step 2: Recovery Plans"));
        
        planModel = new DefaultTableModel(new String[]{"Student ID", "Course ID", "Status"}, 0);
        planTable = new JTable(planModel);
        bottomPanel.add(new JScrollPane(planTable), BorderLayout.CENTER);

        // Button Container
        JPanel btnPanel = new JPanel(new FlowLayout());

        JButton btnGenerate = new JButton("2. Generate Recovery Plans");
        btnGenerate.addActionListener(e -> {
            int[] selectedRows = failedTable.getSelectedRows();
            if (selectedRows.length == 0) {
                JOptionPane.showMessageDialog(null, "Select a student from the top table first.");
            } else {
                for (int row : selectedRows) {
                    String sID = (String) failedModel.getValueAt(row, 0);
                    String cID = (String) failedModel.getValueAt(row, 1);
                    manager.assignRecoveryPlan(sID, cID);
                    planModel.addRow(new Object[]{sID, cID, "Plan Created"});
                }
                JOptionPane.showMessageDialog(null, "Plans Saved!");
            }
        });

        // NEW BUTTON: Manage Tasks
        JButton btnManage = new JButton("3. Manage Tasks / Details");
        btnManage.addActionListener(e -> {
            int row = planTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Please select a plan from the BOTTOM table.");
            } else {
                String sID = (String) planModel.getValueAt(row, 0);
                String cID = (String) planModel.getValueAt(row, 1);
                // Open the new details window
                new PlanDetailsFrame(sID, cID);
            }
        });

        btnPanel.add(btnGenerate);
        btnPanel.add(btnManage);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        add(bottomPanel);
    }
}