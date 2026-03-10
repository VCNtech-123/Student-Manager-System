import java.io.*;
import java.util.ArrayList;

public class SectionManager {
    private ArrayList<Section> sections = new ArrayList<>();

    public ArrayList<Section> getSections() {
        return sections;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Section section) {
        sections.remove(section);
    }

    public void removeSectionByName(String name) {
        Section section = getSectionByName(name);
        if (section != null) {
            removeSection(section);
        }
    }

    public Section getSectionByName(String name) {
        for (Section s : sections) {
            if (s.getSectionName().equals(name)) {
                return s;
            }
        }
        return null;
    }

	//Methods and Save
    public java.util.List<Student> getAllStudents() {
        ArrayList<Student> all = new ArrayList<>();
        for (Section s : sections) {
            all.addAll(s.getStudents());
        }
        return all;
    }

    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Section section : sections) {
                writer.write("SECTION:" + section.getSectionName());
                writer.newLine();
                for (Student student : section.getStudents()) {
                    // name,course,grade
                    writer.write(student.getName() + "," +
                                 student.getCourse() + "," +
                                 student.getAverageGrade());
                    writer.newLine();
                }
                writer.write("ENDSECTION");
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String filename) {
        sections.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            Section currentSection = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("SECTION:")) {
                    String sectionName = line.substring("SECTION:".length());
                    currentSection = new Section(sectionName);
                    sections.add(currentSection);
                } else if (line.equals("ENDSECTION")) {
                    currentSection = null;
                } else if (currentSection != null && !line.trim().isEmpty()) {
                    String[] parts = line.split(",");

                    if (parts.length >= 2) {
                        String name = parts[0];
                        String course;
                        double grade;

                        if (parts.length == 2) {
                            course = "N/A";
                            grade = Double.parseDouble(parts[1]);
                        } else {
                            course = parts[1];
                            grade = Double.parseDouble(parts[2]);
                        }

                        currentSection.addStudent(new Student(name, course, grade));
                    }
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}