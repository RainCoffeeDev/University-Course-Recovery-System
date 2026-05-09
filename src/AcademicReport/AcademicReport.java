package AcademicReport;

import java.io.*;
import java.util.*;

/**
 *
 * @author Runa Yamada
 */
public class AcademicReport {

    public static class Course {
        public String code, title, grade;
        public int credit;
        public double point;

        public Course(String code, String title, int credit, String grade, double point) {
            this.code = code;
            this.title = title;
            this.credit = credit;
            this.grade = grade;
            this.point = point;
        }
    }

    public static class ReportData {
        public String studentId;
        public String name;
        public String program;
        public ArrayList<Course> courses = new ArrayList<>();
        public double cgpa;
    }

    // Grade-to-point reference
    private static final Map<String, Double> gradeMap = new HashMap<>();
    static {
        gradeMap.put("A+", 4.00);
        gradeMap.put("A", 3.70);
        gradeMap.put("B+", 3.30);
        gradeMap.put("B", 3.00);
        gradeMap.put("C+", 2.70);
        gradeMap.put("C", 2.30);
        gradeMap.put("C-", 2.00);
        gradeMap.put("D", 1.70);
        gradeMap.put("F+", 1.30);
        gradeMap.put("F", 1.00);
        gradeMap.put("F-", 0.00);
    }

    // Main function: gathers all report data for a student
    public ReportData getReport(String studentID) {
        ReportData d = new ReportData();
        d.studentId = studentID;

        // Load basic student info + program name
        HashMap<String, String> majorMap = loadMajor();
        try (BufferedReader br = new BufferedReader(new FileReader("students.csv"))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] a = line.split(",");
                if (a[0].equals(studentID)) {
                    String first = a[1];
                    String last = a[2];
                    String majorId = a[3];

                    d.name = first + " " + last;
                    d.program = majorMap.get(majorId);
                }
            }
        } catch (Exception e) {}

        // Collect all grades for the student
        HashMap<String, String> gradeMapCSV = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("grades.csv"))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] a = line.split(",");
                if (a[0].equals(studentID)) {
                    gradeMapCSV.put(a[1], a[2]); // course → grade
                }
            }
        } catch (Exception e) {}

        // Load course list and match with grades
        try (BufferedReader br = new BufferedReader(new FileReader("courses.csv"))) {
            br.readLine(); // skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] a = line.split(",");
                String code = a[0];
                if (gradeMapCSV.containsKey(code)) {
                    String title = a[1];
                    int credit = Integer.parseInt(a[2]);
                    String grade = gradeMapCSV.get(code);
                    double point = gradeMap.getOrDefault(grade, 0.0);

                    d.courses.add(new Course(code, title, credit, grade, point));
                }
            }

        } catch (Exception e) {}

        // CGPA calculation
        double totalP = 0;
        double totalC = 0;

        for (Course c : d.courses) {
            totalP += c.point * c.credit;
            totalC += c.credit;
        }

        d.cgpa = totalC == 0 ? 0 : totalP / totalC;

        return d;
    }

    // Load major.csv into a simple map
    private HashMap<String, String> loadMajor() {
        HashMap<String, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("major.csv"))) {
            br.readLine(); // skip header
            String l;
            while ((l = br.readLine()) != null) {
                String[] a = l.split(",");
                map.put(a[0], a[1]);  // Major Name
            }
        } catch (Exception e) {}
        return map;
    }
}