import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Classroom {
    private ArrayList<Student> students = new ArrayList<>();

    public void addStudent(Student student) {
        students.add(student);
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void sortByName() {
        students.sort(Comparator.comparing(Student::getName));
    }

    public void sortByGrade() {
        students.sort(Comparator.comparingDouble(Student::getAverageGrade).reversed());
    }

    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Student s : students) {
                writer.write(s.getName() + "," + s.getAverageGrade());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadFromFile(String filename) {
        students.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0];
                    double grade = Double.parseDouble(parts[1]);
                    students.add(new Student(name, grade));
                }
            }
        } catch (FileNotFoundException e) {
			
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}