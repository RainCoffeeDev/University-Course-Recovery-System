package CourseRecovery;

import java.util.ArrayList;

public class CourseRecoveryPlan {
    private String studentID;
    private String courseCode;
    private String status;
    private ArrayList<RecoveryMilestone> milestones; 

    public CourseRecoveryPlan(String studentID, String courseCode) {
        this.studentID = studentID;
        this.courseCode = courseCode;
        this.status = "Active";
        this.milestones = new ArrayList<>();
    }

    public void addMilestone(String week, String task) {
        milestones.add(new RecoveryMilestone(week, task));
    }
    
    public String getStudentID() { return studentID; }
    public String getCourseCode() { return courseCode; }
    
    @Override
    public String toString() { return studentID + "," + courseCode + "," + status; }
}