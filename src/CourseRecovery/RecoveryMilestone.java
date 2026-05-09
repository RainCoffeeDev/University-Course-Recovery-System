package CourseRecovery;

public class RecoveryMilestone {
    private String week;
    private String task;

    public RecoveryMilestone(String week, String task) {
        this.week = week;
        this.task = task;
    }

    public String getWeek() { return week; }
    public String getTask() { return task; }
    
    @Override
    public String toString() { return week + ": " + task; }
}