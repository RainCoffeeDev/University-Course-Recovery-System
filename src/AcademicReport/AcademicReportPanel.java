package AcademicReport;

import Email.EmailDialog;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 *
 * @author Runa Yamada
 */
public class AcademicReportPanel extends JPanel {

    // Internal Data Classes
    private static class StudentInfo {
        String id, firstName, lastName, email, majorId;
        int semester;

        StudentInfo(String id, String firstName, String lastName,
                    String email, String majorId, int semester) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.majorId = majorId;
            this.semester = semester;
        }

        String getFullName() { return firstName + " " + lastName; }
    }

    private static class CourseInfo {
        String id, title;
        int credit;

        CourseInfo(String id, String title, int credit) {
            this.id = id;
            this.title = title;
            this.credit = credit;
        }
    }

    // Rounded Button
    private static class RoundedHoverButton extends JButton {
        private final Color normal = new Color(0, 155, 155);
        private final Color hover = new Color(0, 200, 200);

        RoundedHoverButton(String t) {
            super(t);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBackground(normal);

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { setBackground(hover); }
                public void mouseExited(java.awt.event.MouseEvent e) { setBackground(normal); }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // UI COMPONENTS
    private JComboBox<String> cmbStudentId;
    private RoundedHoverButton btnLoad, btnPdf;
    private JTable tblCourses;
    private DefaultTableModel tableModel;

    private JLabel lblStudentName, lblProgram, lblCgpa;

    // Data maps
    private final Map<String, StudentInfo> students = new HashMap<>();
    private final Map<String, String> majors = new HashMap<>();
    private final Map<String, CourseInfo> courses = new HashMap<>();
    private final Map<String, Double> gradePoints = new HashMap<>();

    private double currentCgpa = 0.0;

    // Constructor
    public AcademicReportPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(20, 20));

        initGradePointMap();
        loadStudents();
        loadMajors();
        loadCourses();
        buildUI();

        JScrollPane scroll = new JScrollPane(tblCourses);
        scroll.getViewport().setBackground(new Color(230, 230, 230));
        scroll.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        scroll.setViewportBorder(BorderFactory.createLineBorder(Color.WHITE, 1));

        tblCourses.setShowVerticalLines(true);
        tblCourses.setShowHorizontalLines(true);

        add(scroll, BorderLayout.CENTER);
    }

    // Grade Point Map
    private void initGradePointMap() {
        gradePoints.put("A+", 4.00);
        gradePoints.put("A", 3.70);
        gradePoints.put("B+", 3.30);
        gradePoints.put("B", 3.00);
        gradePoints.put("C+", 2.70);
        gradePoints.put("C", 2.30);
        gradePoints.put("C-", 2.00);
        gradePoints.put("D", 1.70);
        gradePoints.put("F+", 1.30);
        gradePoints.put("F", 1.00);
        gradePoints.put("F-", 0.00);
    }

    // Load students.csv
    private void loadStudents() {
        try (BufferedReader br = new BufferedReader(new FileReader("students.csv"))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 8) continue;

                int semester;
                try { semester = Integer.parseInt(p[7].trim()); }
                catch (Exception ex) { semester = 1; }

                students.put(p[0].trim(), new StudentInfo(
                        p[0].trim(),
                        p[1].trim(),
                        p[2].trim(),
                        p[3].trim(),
                        p[5].trim(),
                        semester
                ));
            }

        } catch (Exception e) {
            showError("Failed to read students.csv\n" + e.getMessage());
        }
    }

    private void loadMajors() {
        try (BufferedReader br = new BufferedReader(new FileReader("major.csv"))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length >= 2) majors.put(p[0].trim(), p[1].trim());
            }

        } catch (Exception e) {
            showError("Failed to read major.csv\n" + e.getMessage());
        }
    }

    private void loadCourses() {
        try (BufferedReader br = new BufferedReader(new FileReader("courses.csv"))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 3) continue;

                int credit;
                try { credit = Integer.parseInt(p[2].trim()); }
                catch (Exception ex) { credit = 0; }

                courses.put(p[0].trim(), new CourseInfo(p[0].trim(), p[1].trim(), credit));
            }

        } catch (Exception e) {
            showError("Failed to read courses.csv\n" + e.getMessage());
        }
    }

    // Build UI
    private void buildUI() {

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel title = new JLabel("Academic Performance Reporting");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        top.add(title, BorderLayout.NORTH);

        JPanel ctr = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        ctr.setOpaque(false);

        JLabel lblId = new JLabel("Student ID:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblId.setForeground(Color.WHITE);

        cmbStudentId = new JComboBox<>();
        students.keySet().stream().sorted().forEach(cmbStudentId::addItem);
        cmbStudentId.setPreferredSize(new Dimension(100, 30));
        cmbStudentId.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btnLoad = new RoundedHoverButton("Load Report");
        btnLoad.setPreferredSize(new Dimension(140, 32));
        btnLoad.addActionListener(this::onLoadReport);

        ctr.add(lblId);
        ctr.add(cmbStudentId);
        ctr.add(btnLoad);
        top.add(ctr, BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);

        String[] cols = {"Course ID", "Course Title", "Credit", "Grade", "Point"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tblCourses = new JTable(tableModel);
        styleTable(tblCourses);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        lblStudentName = new JLabel("Student Name:");
        lblProgram = new JLabel("Program:");
        lblCgpa = new JLabel("CGPA:");

        lblStudentName.setForeground(Color.WHITE);
        lblProgram.setForeground(Color.WHITE);
        lblCgpa.setForeground(new Color(255, 215, 0));

        lblStudentName.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblProgram.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCgpa.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        info.add(lblStudentName);
        info.add(lblProgram);
        info.add(Box.createVerticalStrut(5));
        info.add(lblCgpa);

        bottom.add(info, BorderLayout.WEST);

        btnPdf = new RoundedHoverButton("Generate PDF Report");
        btnPdf.setPreferredSize(new Dimension(190, 38));
        btnPdf.addActionListener(this::onGeneratePdf);

        JPanel pdfPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pdfPanel.setOpaque(false);
        pdfPanel.add(btnPdf);

        bottom.add(pdfPanel, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }

    // Table Style
    private void styleTable(JTable t) {
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setRowHeight(26);
        t.setBackground(new Color(30, 30, 50));
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(60, 60, 80));

        JTableHeader h = t.getTableHeader();
        h.setBackground(new Color(10, 20, 40));
        h.setForeground(Color.WHITE);
        h.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private void resizeColumns() {
        TableColumnModel cm = tblCourses.getColumnModel();
        cm.getColumn(0).setPreferredWidth(80);
        cm.getColumn(1).setPreferredWidth(300);
        cm.getColumn(2).setPreferredWidth(60);
        cm.getColumn(3).setPreferredWidth(70);
        cm.getColumn(4).setPreferredWidth(70);
    }

    // Load Report
    private void onLoadReport(ActionEvent e) {
        String id = (String) cmbStudentId.getSelectedItem();
        if (id == null || !students.containsKey(id)) {
            showError("Please select a valid Student ID.");
            return;
        }

        StudentInfo stu = students.get(id);
        tableModel.setRowCount(0);

        double q = 0;
        int csum = 0;

        try (BufferedReader br = new BufferedReader(new FileReader("grades.csv"))) {
            br.readLine();
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(",", -1);
                if (p.length < 3) continue;
                if (!p[0].trim().equals(id)) continue;

                CourseInfo ci = courses.get(p[1].trim());
                if (ci == null) continue;

                String grade = p[2].trim();
                double point = gradePoints.getOrDefault(grade, 0.0);

                tableModel.addRow(new Object[]{
                        ci.id, ci.title, ci.credit, grade, String.format(java.util.Locale.US, "%.2f", point)
                });

                q += point * ci.credit;
                csum += ci.credit;
            }

        } catch (Exception ex) {
            showError("Failed to load grades.csv\n" + ex.getMessage());
        }

        lblStudentName.setText("Student Name: " + stu.getFullName());
        lblProgram.setText("Program: Bachelor of " + majors.getOrDefault(stu.majorId, "Unknown"));

        currentCgpa = (csum > 0) ? q / csum : 0.0;
        lblCgpa.setText(String.format(java.util.Locale.US, "CGPA: %.2f", currentCgpa));

        resizeColumns();
    }

    // Generate PDF
    private void onGeneratePdf(ActionEvent e) {
        String id = (String) cmbStudentId.getSelectedItem();
        if (id == null || tableModel.getRowCount() == 0) {
            showError("Load a report first.");
            return;
        }

        StudentInfo stu = students.get(id);
        String majorName = majors.getOrDefault(stu.majorId, "Unknown");

        List<ReportPDFWriter.CourseLine> lines = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            
            String pointText = tableModel.getValueAt(i, 4).toString().trim();
            pointText = pointText.replace(",", ".");
            double pointValue = 0.0;
            try {
                pointValue = Double.parseDouble(pointText);
            } catch (Exception ex) {
                pointValue = 0.0;
            }

            lines.add(new ReportPDFWriter.CourseLine(
                    tableModel.getValueAt(i, 0).toString(),
                    tableModel.getValueAt(i, 1).toString(),
                    Integer.parseInt(tableModel.getValueAt(i, 2).toString()),
                    tableModel.getValueAt(i, 3).toString(),
                    pointValue
            ));
        }

        try {
            String path = ReportPDFWriter.writePDF(
                    id,
                    stu.getFullName(),
                    "Bachelor of " + majorName,
                    "Semester " + stu.semester,   // correct semester
                    lines,
                    currentCgpa
            );

            int op = JOptionPane.showConfirmDialog(
                    this,
                    "PDF created.\nEmail this report?",
                    "Send Email?",
                    JOptionPane.YES_NO_OPTION
            );

            if (op == JOptionPane.YES_OPTION) {
                EmailDialog dialog = new EmailDialog(
                        (Frame) SwingUtilities.getWindowAncestor(this),
                        stu.getFullName(),
                        stu.id,
                        stu.email,
                        path
                );
                dialog.setVisible(true);
            }

        } catch (Exception ex) {
            showError("PDF generation failed.\n" + ex.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}