import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Section {
    private String sectionName;
    private ArrayList<Student> students = new ArrayList<>();

    public Section(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void removeStudent(Student student) {
        students.remove(student);
    }

    // Remove first student with this name
    public void removeStudentByName(String name) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getName().equals(name)) {
                students.remove(i);
                break;
            }
        }
    }

    public void sortByName() {
        students.sort(Comparator.comparing(Student::getName));
    }

    public void sortByGrade() {
        students.sort(Comparator.comparingDouble(Student::getAverageGrade).reversed());
    }

    // ---- Statistics ----

    public double getMeanGrade() {
        if (students.isEmpty()) return Double.NaN;
        double sum = 0;
        for (Student s : students) {
            sum += s.getAverageGrade();
        }
        return sum / students.size();
    }

    public double getMedianGrade() {
        if (students.isEmpty()) return Double.NaN;
        List<Double> grades = new ArrayList<>();
        for (Student s : students) {
            grades.add(s.getAverageGrade());
        }
        Collections.sort(grades);
        int n = grades.size();
        if (n % 2 == 1) {
            return grades.get(n / 2);
        } else {
            return (grades.get(n / 2 - 1) + grades.get(n / 2)) / 2.0;
        }
    }

    public Double getModeGrade() {
        if (students.isEmpty()) return null;
        Map<Double, Integer> freq = new HashMap<>();
        for (Student s : students) {
            double g = s.getAverageGrade();
            freq.put(g, freq.getOrDefault(g, 0) + 1);
        }
        int maxCount = 0;
        Double mode = null;
        for (Map.Entry<Double, Integer> e : freq.entrySet()) {
            if (e.getValue() > maxCount) {
                maxCount = e.getValue();
                mode = e.getKey();
            }
        }
        return mode;
    }

    public Student getTopStudent() {
        if (students.isEmpty()) return null;
        return students.stream()
                .max(Comparator.comparingDouble(Student::getAverageGrade))
                .orElse(null);
    }

    public Student getLowestStudent() {
        if (students.isEmpty()) return null;
        return students.stream()
                .min(Comparator.comparingDouble(Student::getAverageGrade))
                .orElse(null);
    }
}