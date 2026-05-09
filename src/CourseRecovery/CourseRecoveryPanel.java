package CourseRecovery;

import MainMenu.MainMenuFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import MainMenu.MainMenuFrame.RoundedHoverButton;

public class CourseRecoveryPanel extends JPanel {

    private final MainMenuFrame parent;
    private final CourseRecoveryManager manager;

    private JTable failedTable, planTable;
    private DefaultTableModel failedModel, planModel;

    public CourseRecoveryPanel(MainMenuFrame parent) {
        this.parent = parent;
        this.manager = new CourseRecoveryManager();

        setOpaque(false);
        setLayout(new BorderLayout(10, 10));

        JPanel topText = new JPanel();
        topText.setOpaque(false);
        topText.setLayout(new BoxLayout(topText, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Course Recovery Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        JLabel desc = new JLabel("Identify failed students and assign recovery milestones.");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        desc.setForeground(new Color(210, 210, 210));

        topText.add(title);
        topText.add(Box.createVerticalStrut(3));
        topText.add(desc);
        add(topText, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new GridLayout(2, 1, 0, 20));
        mainContent.setOpaque(false);

        // TOP: Failures
        JPanel failPanel = createSectionPanel("Detected Failures (From course_recovery.csv)");
        failedModel = new DefaultTableModel(new String[]{"Student ID", "Course ID", "Grade"}, 0);
        failedTable = styleTable(new JTable(failedModel));
        failPanel.add(new JScrollPane(failedTable), BorderLayout.CENTER);

        RoundedHoverButton btnScan = new RoundedHoverButton("Scan for Failures");
        btnScan.addActionListener(e -> scanFailures());
        JPanel pnlScan = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlScan.setOpaque(false);
        pnlScan.add(btnScan);
        failPanel.add(pnlScan, BorderLayout.SOUTH);

        mainContent.add(failPanel);

        // BOTTOM: Plans
        JPanel planPanel = createSectionPanel("Active Recovery Plans");
        planModel = new DefaultTableModel(new String[]{"Student ID", "Course ID", "Status"}, 0);
        planTable = styleTable(new JTable(planModel));
        planPanel.add(new JScrollPane(planTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setOpaque(false);

        RoundedHoverButton btnGen  = new RoundedHoverButton("Generate Plan");
        RoundedHoverButton btnTask = new RoundedHoverButton("Manage Tasks");

        btnGen.addActionListener(e -> generatePlans());
        btnTask.addActionListener(e -> openTaskDetails());

        btnPanel.add(btnGen);
        btnPanel.add(btnTask);
        planPanel.add(btnPanel, BorderLayout.SOUTH);

        mainContent.add(planPanel);

        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createSectionPanel(String titleStr) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(100, 100, 150)),
                titleStr,
                0, 0,
                new Font("Segoe UI", Font.BOLD, 12),
                Color.WHITE));
        return p;
    }

    private JTable styleTable(JTable table) {
        table.setBackground(new Color(30, 30, 50));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(60, 60, 80));
        table.setSelectionBackground(new Color(0, 155, 155));
        table.setSelectionForeground(Color.WHITE);
        table.setRowHeight(25);
        table.getTableHeader().setBackground(new Color(10, 20, 40));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        return table;
    }

    private void scanFailures() {
        failedModel.setRowCount(0);
        java.util.ArrayList<CourseRecoveryManager.FailedStudent> list = manager.getFailedStudents();
        for (CourseRecoveryManager.FailedStudent s : list) {
            failedModel.addRow(new Object[]{s.studentID, s.courseID, s.grade});
        }
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No failures found.");
        }
    }

    private void generatePlans() {
        int[] rows = failedTable.getSelectedRows();
        if (rows.length == 0) {
            JOptionPane.showMessageDialog(this, "Select a student from the top table.");
            return;
        }
        for (int r : rows) {
            String sID = (String) failedModel.getValueAt(r, 0);
            String cID = (String) failedModel.getValueAt(r, 1);
            manager.assignRecoveryPlan(sID, cID);
            planModel.addRow(new Object[]{sID, cID, "Plan Created"});
        }
        JOptionPane.showMessageDialog(this, "Plans saved to recovery_plans.csv");
    }

    private void openTaskDetails() {
        int r = planTable.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Select a plan from the bottom table.");
            return;
        }

        String sID = (String) planModel.getValueAt(r, 0);
        String cID = (String) planModel.getValueAt(r, 1);

        parent.openTaskDetails(sID, cID);
    }
}
