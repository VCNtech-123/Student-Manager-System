

public class Student {
    private String name;
    private String course;
    private double averageGrade;

    public Student(String name, String course, double averageGrade) {
        this.name = name;
        this.course = course;
        this.averageGrade = averageGrade;
    }

    public Student(String name, double averageGrade) {
        this(name, "N/A", averageGrade);
    }

    public String getName() {
        return name;
    }

    public String getCourse() {
        return course;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setAverageGrade(double averageGrade) {
        this.averageGrade = averageGrade;
    }

    @Override
    public String toString() {
        return name + " (" + course + ") - " + averageGrade;
    }
}