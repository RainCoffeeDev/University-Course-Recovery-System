package CourseRecovery;

import java.io.*;
import java.util.ArrayList;

public class CourseRecoveryManager {
    
    // FILES MUST BE IN YOUR PROJECT FOLDER (Outside src)
    private String gradeFile = "course_recovery.csv";
    private String planFile = "recovery_plans.csv";

    // Helper class for data found in grades.csv
    public class FailedStudent {
        String studentID, courseID, grade;
        public FailedStudent(String s, String c, String g) {
            this.studentID = s; this.courseID = c; this.grade = g;
        }
    }

    // 1. SCAN: Reads 'grades.csv' to find "F"
    public ArrayList<FailedStudent> getFailedStudents() {
        ArrayList<FailedStudent> failedList = new ArrayList<>();
        
        // Check if file exists first
        File file = new File(gradeFile);
        if (!file.exists()) {
            System.out.println("Error: course_recovery.csv not found!");
            return failedList;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(gradeFile))) {
            String line;
            br.readLine(); // Skip the header row
            
            while ((line = br.readLine()) != null) {
                // Split the row by comma
                String[] data = line.split(","); 
                
                // The file has at least 3 columns (ID, Course, Grade)
                if (data.length >= 3) {
                    String sID = data[0];       // Column 1: StudentID
                    String cID = data[1];       // Column 2: CourseID
                    String grade = data[2];     // Column 3: Grade 

                    // Debug print to ensure it's reading correctly (Check Output Window)
                    // System.out.println("Checking: " + sID + " Grade: " + grade);

                    // Check for F (using trim() removes accidental spaces)
                    if (grade.trim().equalsIgnoreCase("F") || grade.trim().equalsIgnoreCase("Fail")) {
                        failedList.add(new FailedStudent(sID, cID, grade));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading grades: " + e.getMessage());
        }
        return failedList;
    }

    // 2. WRITE: Saves new plan to 'recovery_plans.csv'
    public void assignRecoveryPlan(String studentID, String courseID) {
        try (FileWriter fw = new FileWriter(planFile, true); 
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(studentID + "," + courseID + ",Active,Week 1,Review Failed Component");
        } catch (IOException e) {
            System.out.println("Error writing plan: " + e.getMessage());
        }
    }
}