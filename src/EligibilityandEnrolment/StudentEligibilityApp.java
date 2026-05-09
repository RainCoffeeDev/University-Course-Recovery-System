package EligibilityandEnrolment;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.text.DecimalFormat;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class StudentEligibilityApp extends JPanel {

    // --- Configuration ---
    private static final String DATA_FILE_NAME = "grades.csv";
    private static final String COURSE_RECOVERY_FILE = "course_recovery.csv";

    // --- Data Structure ---
    private final Map<String, StudentRecord> studentData;

    // --- UI Components ---
    private JComboBox<String> cmbStudentId;
    private JTextArea resultArea;
    private String[] studentIdOptions;

    // --- Inner Class to hold student data ---
    private static class StudentRecord {
        final String name;
        final double cgpa;
        final int failedCourses;

        public StudentRecord(String name, double cgpa, int failedCourses) {
            this.name = name;
            this.cgpa = cgpa;
            this.failedCourses = failedCourses;
        }
    }

    // =========================================================
    //                     CONSTRUCTOR
    // =========================================================
    public StudentEligibilityApp() {

        // 1) Load CSV data into memory
        studentData = loadData();

        // 2) Build list of Student IDs for dropdown
        java.util.List<String> ids = new java.util.ArrayList<>(studentData.keySet());
        java.util.Collections.sort(ids);
        studentIdOptions = ids.toArray(new String[0]);

        // 3) Panel layout & outer background
        setOpaque(false);
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // 4) Top lookup bar (title + row)
        JPanel lookupPanel = createLookupPanel();
        add(lookupPanel, BorderLayout.NORTH);

        // 5) Inner content box (solid dark like User Management list)
        JPanel innerBox = new JPanel(new BorderLayout());
        innerBox.setOpaque(true);
        innerBox.setBackground(new Color(10, 20, 40)); // dark navy
        innerBox.setBorder(BorderFactory.createLineBorder(new Color(80, 100, 140), 1));

        // Result area inside the inner box
        resultArea = new JTextArea(15, 50);
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultArea.setForeground(Color.WHITE);
        resultArea.setBackground(new Color(20, 30, 50));
        resultArea.setCaretColor(Color.WHITE);
        resultArea.setEditable(false);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        innerBox.add(scrollPane, BorderLayout.CENTER);

        add(innerBox, BorderLayout.CENTER);
    }

    // =========================================================
    //        BUILD LOOKUP PANEL TO MATCH ACADEMIC REPORT UI
    // =========================================================
    private JPanel createLookupPanel() {
        JPanel root = new JPanel();
        root.setOpaque(false);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        // --- Title row ---
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);

        JLabel title = new JLabel("Student Eligibility & Enrolment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(Color.WHITE);
        titleRow.add(title);

        root.add(titleRow);
        root.add(Box.createVerticalStrut(10));

        // --- Student ID row (label + combo + button) ---
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setOpaque(false);

        JLabel lbl = new JLabel("Student ID:");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lbl.setForeground(Color.WHITE);
        row.add(lbl);

        cmbStudentId = new JComboBox<>(studentIdOptions);
        cmbStudentId.setPreferredSize(new Dimension(90, 28));
        cmbStudentId.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        row.add(cmbStudentId);

        TealRoundedButton btnLookup = new TealRoundedButton("Check Eligibility");
        btnLookup.setPreferredSize(new Dimension(160, 32));
        btnLookup.addActionListener(e -> lookupEligibility());
        row.add(btnLookup);

        root.add(row);

        return root;
    }

    // =========================================================
    //      LOAD DATA FROM grades.csv AND COMPUTE CGPA
    // =========================================================
    private Map<String, StudentRecord> loadData() {

        Map<String, StudentRecord> data = new HashMap<>();

        Map<String, Double> totalPoints = new HashMap<>();
        Map<String, Integer> courseCounts = new HashMap<>();
        Map<String, Double> cgpaFromFile = new HashMap<>();
        Map<String, Integer> failedCoursesMap = new HashMap<>();
        Set<String> studentIds = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE_NAME))) {

            String header = br.readLine();
            if (header == null)
                throw new IOException("grades.csv is empty");

            String[] headerParts = header.split(",", -1);

            int idxStudent = -1;
            int idxGradePoint = -1;
            int idxOverallCgpa = -1;

            for (int i = 0; i < headerParts.length; i++) {
                String col = headerParts[i].trim().toLowerCase();
                if (col.equals("studentid")) idxStudent = i;
                else if (col.startsWith("grade point")) idxGradePoint = i;
                else if (col.startsWith("overall cgpa")) idxOverallCgpa = i;
            }

            if (idxStudent == -1 || idxGradePoint == -1)
                throw new IOException("Missing StudentID or Grade Point column");

            String line;
            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length <= idxStudent) continue;

                String studentId = parts[idxStudent].trim().toUpperCase();
                if (studentId.isEmpty()) continue;

                studentIds.add(studentId);

                // Grade point per course
                double gradePoint = Double.NaN;
                if (!parts[idxGradePoint].trim().isEmpty()) {
                    try {
                        gradePoint = Double.parseDouble(parts[idxGradePoint].trim());
                    } catch (Exception ignored) {}
                }

                // Official CGPA if present
                if (idxOverallCgpa != -1 && parts.length > idxOverallCgpa
                        && !parts[idxOverallCgpa].trim().isEmpty()) {
                    try {
                        cgpaFromFile.put(studentId,
                                Double.parseDouble(parts[idxOverallCgpa].trim()));
                    } catch (Exception ignored) {}
                }

                // Aggregation for computed CGPA
                if (!Double.isNaN(gradePoint)) {
                    totalPoints.put(studentId,
                            totalPoints.getOrDefault(studentId, 0.0) + gradePoint);

                    courseCounts.put(studentId,
                            courseCounts.getOrDefault(studentId, 0) + 1);

                    // Failing: grade point < 2.0
                    if (gradePoint < 2.0) {
                        failedCoursesMap.put(studentId,
                                failedCoursesMap.getOrDefault(studentId, 0) + 1);
                    }
                }
            }

            // Build final StudentRecord for each student
            for (String id : studentIds) {
                double cgpa;

                if (cgpaFromFile.containsKey(id)) {
                    cgpa = cgpaFromFile.get(id);
                } else {
                    double total = totalPoints.getOrDefault(id, 0.0);
                    int count = courseCounts.getOrDefault(id, 0);
                    cgpa = (count > 0) ? (total / count) : 0.0;
                }

                int failed = failedCoursesMap.getOrDefault(id, 0);
                data.put(id, new StudentRecord("Student " + id, cgpa, failed));
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Could not read " + DATA_FILE_NAME + "\n\n" + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return data;
    }

    // =========================================================
    //                      LOOKUP LOGIC
    // =========================================================
    private void lookupEligibility() {

        if (resultArea != null) {
            resultArea.setText("");
        }

        String studentId = (String) cmbStudentId.getSelectedItem();
        if (studentId == null || studentId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No Student ID selected.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        studentId = studentId.trim().toUpperCase();

        StudentRecord record = studentData.get(studentId);

        if (record == null) {
            resultArea.setText("Student ID " + studentId + " not found in dataset.");
            return;
        }

        String name = record.name;
        double cgpa = record.cgpa;
        int failedCourses = record.failedCourses;
        DecimalFormat df = new DecimalFormat("0.00");

        boolean cgpaEligible = cgpa >= 2.0;
        boolean failedEligible = failedCourses <= 3;
        boolean eligible = cgpaEligible && failedEligible;

        StringBuilder sb = new StringBuilder();
        sb.append("--- Eligibility Check Results for ").append(name)
          .append(" (ID: ").append(studentId).append(") ---\n\n");

        sb.append("Student Data (from grades.csv):\n");
        sb.append("• CGPA: ").append(df.format(cgpa)).append("\n");
        sb.append("• Total Failed Courses (GP < 2.0): ").append(failedCourses).append("\n\n");

        sb.append("Eligibility Criteria:\n");
        sb.append("• CGPA >= 2.0: ").append(cgpaEligible ? "PASS " : "FAIL ").append("\n");
        sb.append("• Failed Courses <= 3: ").append(failedEligible ? "PASS " : "FAIL ").append("\n\n");

        if (eligible) {
            sb.append("====================================\n");
            sb.append("   STUDENT IS ELIGIBLE TO PROGRESS\n");
            sb.append("   >>> Registration Confirmed <<<\n");
            sb.append("====================================\n");

            JOptionPane.showMessageDialog(this,
                    name + " (" + studentId + ") is ELIGIBLE to progress.",
                    "Eligibility Result",
                    JOptionPane.INFORMATION_MESSAGE);

        } else {
            sb.append("====================================\n");
            sb.append("   NOT ELIGIBLE TO PROGRESS\n");
            sb.append("   >>> Must Re-enroll / Repeat <<<\n");
            sb.append("====================================\n");

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    name + " (" + studentId + ") is NOT eligible.\n\n" +
                    "Add ALL failing courses for this student to " + COURSE_RECOVERY_FILE + "?",
                    "Not Eligible - Send to Course Recovery",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (choice == JOptionPane.YES_OPTION) {
                appendToCourseRecoveryCsv(studentId);
            }
        }

        resultArea.setText(sb.toString());
    }

    // =========================================================
    //   APPEND ONLY FAILING COURSES TO RECOVERY CSV
    //      (Grade Point < 2.0, output StudentID,CourseID,Grade)
    //   + SKIP if student already exists in course_recovery.csv
    // =========================================================
    private void appendToCourseRecoveryCsv(String studentId) {
        File file = new File(COURSE_RECOVERY_FILE);

        // ---------- 1. Check for existing student to avoid duplicates ----------
        if (file.exists()) {
            try (BufferedReader brRec = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = brRec.readLine()) != null) {
                    // skip header
                    if (line.toLowerCase().startsWith("studentid")) continue;

                    String[] parts = line.split(",", -1);
                    if (parts.length > 0) {
                        String sid = parts[0].trim();
                        if (sid.equalsIgnoreCase(studentId)) {
                            JOptionPane.showMessageDialog(
                                    this,
                                    "Student " + studentId + " is already in " + COURSE_RECOVERY_FILE +
                                    ".\nNo duplicate entries were added.",
                                    "Already Added",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            return; // don’t add again
                        }
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Could not read " + COURSE_RECOVERY_FILE + ":\n" + e.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return; // bail out if we can't safely check
            }
        }

        // ---------- 2. If not found, append failing courses from grades.csv ----------
        boolean exists = file.exists();

        try (
                BufferedReader br = new BufferedReader(new FileReader(DATA_FILE_NAME));
                FileWriter fw = new FileWriter(file, true);
                PrintWriter pw = new PrintWriter(fw)
        ) {
            // Read header from grades.csv
            String header = br.readLine();
            if (header == null) {
                throw new IOException("grades.csv is empty");
            }

            String[] headerParts = header.split(",", -1);
            int idxStudent = -1;
            int idxCourse  = -1;
            int idxGrade   = -1;
            int idxGradePt = -1;  // Grade Point (4.0 Scale)

            for (int i = 0; i < headerParts.length; i++) {
                String col = headerParts[i].trim().toLowerCase();
                if (col.equals("studentid")) idxStudent = i;
                else if (col.equals("courseid")) idxCourse = i;
                else if (col.equals("grade")) idxGrade = i;
                else if (col.startsWith("grade point")) idxGradePt = i;
            }

            if (idxStudent == -1 || idxCourse == -1 || idxGrade == -1 || idxGradePt == -1) {
                throw new IOException(
                        "grades.csv must contain StudentID, CourseID, Grade, and Grade Point columns");
            }

            // Write header for course_recovery.csv if new file
            if (!exists) {
                pw.println("StudentID,CourseID,Grade");
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                int maxIdx = Math.max(Math.max(idxStudent, idxCourse), Math.max(idxGrade, idxGradePt));
                if (parts.length <= maxIdx) {
                    continue;
                }

                String sid = parts[idxStudent].trim();
                if (!sid.equalsIgnoreCase(studentId)) continue;

                String gpStr = parts[idxGradePt].trim();
                if (gpStr.isEmpty()) continue;

                double gp;
                try {
                    gp = Double.parseDouble(gpStr);
                } catch (NumberFormatException ex) {
                    continue; // skip weird rows
                }

                // ONLY courses with grade point < 2.0 go into course_recovery.csv
                if (gp < 2.0) {
                    String courseId = parts[idxCourse].trim();
                    String grade    = parts[idxGrade].trim();
                    pw.println(studentId + "," + courseId + "," + grade);
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Failing courses for student " + studentId +
                    " have been added to " + COURSE_RECOVERY_FILE,
                    "Course Recovery Updated",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Could not write to " + COURSE_RECOVERY_FILE + ":\n" + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    //         CUSTOM TEAL ROUNDED BUTTON (LIKE YOUR UI)
    // =========================================================
    private static class TealRoundedButton extends JButton {

        public TealRoundedButton(String text) {
            super(text);
            setForeground(Color.WHITE);
            setBackground(new Color(0, 155, 155));
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            super.paintComponent(g2);
            g2.dispose();
        }
    }
}

