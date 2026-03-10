

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class StudentManagerSystem extends JFrame {

    private static final String SAVE_FILE = "sections.txt";

    private final SectionManager sectionManager = new SectionManager();

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    private SectionListPanel sectionListPanel;
    private StudentListPanel studentListPanel;
    private SectionsInfoPanel sectionsInfoPanel;

    private final Color primaryBlue = new Color(33, 150, 243);
    private final Color darkBlue = new Color(25, 118, 210);
    private final Color lightBackground = new Color(227, 242, 253);

    public StudentManagerSystem() {
        // Load data
        sectionManager.loadFromFile(SAVE_FILE);

        setTitle("Student Manager System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        getContentPane().setBackground(lightBackground);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        headerPanel.setBackground(primaryBlue);

        JLabel titleLabel = new JLabel("Student Manager System");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        JButton sectionButton = createNavButton("Section List");
        JButton studentsButton = createNavButton("Students List");
        JButton infoButton = createNavButton("Sections Info");
        JButton exitButton = createNavButton("Exit");

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createHorizontalStrut(30));
        headerPanel.add(sectionButton);
        headerPanel.add(studentsButton);
        headerPanel.add(infoButton);
        headerPanel.add(exitButton);

        add(headerPanel, BorderLayout.NORTH);

        sectionListPanel = new SectionListPanel();
        studentListPanel = new StudentListPanel();
        sectionsInfoPanel = new SectionsInfoPanel();

        contentPanel.setBackground(lightBackground);
        contentPanel.add(sectionListPanel, "SECTIONS");
        contentPanel.add(studentListPanel, "STUDENTS");
        contentPanel.add(sectionsInfoPanel, "INFO");

        add(contentPanel, BorderLayout.CENTER);

        sectionButton.addActionListener(e -> showPanel("SECTIONS"));
        studentsButton.addActionListener(e -> showPanel("STUDENTS"));
        infoButton.addActionListener(e -> showPanel("INFO"));
        exitButton.addActionListener(e -> {
            saveData();
            System.exit(0);
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveData();
            }
        });

        dataChanged();
        showPanel("SECTIONS");
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(darkBlue);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(button.getFont().deriveFont(Font.BOLD, 14f));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void showPanel(String name) {
        cardLayout.show(contentPanel, name);
    }

    private void saveData() {
        sectionManager.saveToFile(SAVE_FILE);
    }

    private void dataChanged() {
        sectionListPanel.refreshSectionList();
        studentListPanel.refreshData();
        sectionsInfoPanel.refreshData();
        saveData();
    }

    private class SectionListPanel extends JPanel {
        private DefaultListModel<String> sectionListModel;
        private JList<String> sectionList;

        private JPanel seatingPanel;

        SectionListPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(15, 15, 15, 15));
            setBackground(lightBackground);

            // Left: Sections list
            JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
            leftPanel.setBackground(lightBackground);

            JLabel sectionsLabel = new JLabel("Sections");
            sectionsLabel.setFont(sectionsLabel.getFont().deriveFont(Font.BOLD, 16f));

            sectionListModel = new DefaultListModel<>();
            sectionList = new JList<>(sectionListModel);
            sectionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane sectionScroll = new JScrollPane(sectionList);

            JPanel sectionButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            sectionButtonPanel.setBackground(lightBackground);
            JButton addSectionButton = new JButton("Add Section");
            JButton deleteSectionButton = new JButton("Delete Section");
            sectionButtonPanel.add(addSectionButton);
            sectionButtonPanel.add(deleteSectionButton);

            leftPanel.add(sectionsLabel, BorderLayout.NORTH);
            leftPanel.add(sectionScroll, BorderLayout.CENTER);
            leftPanel.add(sectionButtonPanel, BorderLayout.SOUTH);

            JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
            rightPanel.setBackground(lightBackground);

            JLabel studentsLabel = new JLabel("Classroom Seating (Selected Section)");
            studentsLabel.setFont(studentsLabel.getFont().deriveFont(Font.BOLD, 16f));

            seatingPanel = new JPanel(new GridLayout(0, 5, 10, 10));
            seatingPanel.setBackground(lightBackground);

            JScrollPane seatingScroll = new JScrollPane(seatingPanel);

            JPanel studentButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            studentButtonPanel.setBackground(lightBackground);
            JButton addStudentButton = new JButton("Add Student");
            JButton sortByNameButton = new JButton("Sort by Name");
            JButton sortByGradeButton = new JButton("Sort by Grade");
            JButton deleteStudentButton = new JButton("Delete Student");

            studentButtonPanel.add(addStudentButton);
            studentButtonPanel.add(sortByNameButton);
            studentButtonPanel.add(sortByGradeButton);
            studentButtonPanel.add(deleteStudentButton);

            rightPanel.add(studentsLabel, BorderLayout.NORTH);
            rightPanel.add(seatingScroll, BorderLayout.CENTER);
            rightPanel.add(studentButtonPanel, BorderLayout.SOUTH);

            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
            splitPane.setResizeWeight(0.3);
            add(splitPane, BorderLayout.CENTER);


            sectionList.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    refreshStudentsForSelectedSection();
                }
            });

            addSectionButton.addActionListener(e -> {
                String name = JOptionPane.showInputDialog(StudentManagerSystem.this,
                        "Enter section name:", "Add Section", JOptionPane.PLAIN_MESSAGE);
                if (name != null) {
                    name = name.trim();
                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(StudentManagerSystem.this,
                                "Section name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (sectionManager.getSectionByName(name) != null) {
                        JOptionPane.showMessageDialog(StudentManagerSystem.this,
                                "Section already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    sectionManager.addSection(new Section(name));
                    dataChanged();
                    sectionList.setSelectedValue(name, true);
                }
            });

            deleteSectionButton.addActionListener(e -> {
                String selected = sectionList.getSelectedValue();
                if (selected == null) {
                    JOptionPane.showMessageDialog(StudentManagerSystem.this,
                            "No section selected.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(StudentManagerSystem.this,
                        "Delete section '" + selected + "' and all its students?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    sectionManager.removeSectionByName(selected);
                    dataChanged();
                }
            });

            addStudentButton.addActionListener(e -> {
                String selectedSectionName = sectionList.getSelectedValue();
                if (selectedSectionName == null) {
                    JOptionPane.showMessageDialog(StudentManagerSystem.this,
                            "Please select a section first.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Section section = sectionManager.getSectionByName(selectedSectionName);
                if (section == null) return;
                showAddStudentDialog(section);
            });

            sortByNameButton.addActionListener(e -> {
                String selectedSectionName = sectionList.getSelectedValue();
                if (selectedSectionName == null) {
                    JOptionPane.showMessageDialog(StudentManagerSystem.this,
                            "Please select a section first.", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                }
                Section section = sectionManager.getSectionByName(selectedSectionName);
                if (section == null) return;
                section.sortByName();
                dataChanged();
                sectionList.setSelectedValue(selectedSectionName, true);
            });

            sortByGradeButton.addActionListener(e -> {
                String selectedSectionName = sectionList.getSelectedValue();
                if (selectedSectionName == null) {
                    JOptionPane.showMessageDialog(StudentManagerSystem.this,
                            "Please select a section first.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Section section = sectionManager.getSectionByName(selectedSectionName);
                if (section == null) return;
                section.sortByGrade();
                dataChanged();
                sectionList.setSelectedValue(selectedSectionName, true);
            });

            deleteStudentButton.addActionListener(e -> {
                String selectedSectionName = sectionList.getSelectedValue();
                if (selectedSectionName == null) {
                    JOptionPane.showMessageDialog(StudentManagerSystem.this,
                            "Please select a section first.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Section section = sectionManager.getSectionByName(selectedSectionName);
                if (section == null) return;

                java.util.List<Student> students = section.getStudents();
                if (students.isEmpty()) {
                    JOptionPane.showMessageDialog(StudentManagerSystem.this,
                            "No students in this section.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                String[] names = new String[students.size()];
                for (int i = 0; i < students.size(); i++) {
                    names[i] = students.get(i).getName();
                }

                String nameToDelete = (String) JOptionPane.showInputDialog(
                        StudentManagerSystem.this,
                        "Select student to delete:",
                        "Delete Student",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        names,
                        names[0]
                );

                if (nameToDelete != null) {
                    section.removeStudentByName(nameToDelete);
                    dataChanged();
                    sectionList.setSelectedValue(selectedSectionName, true);
                }
            });
        }

        void refreshSectionList() {
            sectionListModel.clear();
            for (Section s : sectionManager.getSections()) {
                sectionListModel.addElement(s.getSectionName());
            }
            if (!sectionListModel.isEmpty() && sectionList.getSelectedIndex() == -1) {
                sectionList.setSelectedIndex(0);
            }
            refreshStudentsForSelectedSection();
        }

        void refreshStudentsForSelectedSection() {
            seatingPanel.removeAll();

            String selectedSectionName = sectionList.getSelectedValue();
            if (selectedSectionName == null) {
                seatingPanel.revalidate();
                seatingPanel.repaint();
                return;
            }

            Section section = sectionManager.getSectionByName(selectedSectionName);
            if (section == null) {
                seatingPanel.revalidate();
                seatingPanel.repaint();
                return;
            }

            for (Student s : section.getStudents()) {
                JButton btn = createStudentButton(s, section.getSectionName());
                seatingPanel.add(btn);
            }

            seatingPanel.revalidate();
            seatingPanel.repaint();
        }

        private JButton createStudentButton(Student s, String sectionName) {
            Icon icon = UIManager.getIcon("OptionPane.informationIcon");

            String text = "<html><center>" + s.getName() +
                    "<br/><span style='font-size:9px;'>" + s.getCourse() + "</span></center></html>";

            JButton btn = new JButton(text, icon);
            btn.setVerticalTextPosition(SwingConstants.BOTTOM);
            btn.setHorizontalTextPosition(SwingConstants.CENTER);

            btn.setPreferredSize(new Dimension(80, 80));
            btn.setMinimumSize(new Dimension(80, 80));
            btn.setMaximumSize(new Dimension(80, 80));

            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 230)));
            btn.setMargin(new Insets(2, 2, 2, 2));

            btn.addActionListener(e -> showStudentInfoWithDeleteDialog(s, sectionName));

            return btn;
        }

        private void showStudentInfoWithDeleteDialog(Student s, String sectionName) {
            StringBuilder sb = new StringBuilder();
            sb.append("Name: ").append(s.getName()).append("\n");
            sb.append("Course: ").append(s.getCourse()).append("\n");
            sb.append("Grade: ").append(s.getAverageGrade()).append("\n");
            if (sectionName != null) {
                sb.append("Section: ").append(sectionName);
            }

            Object[] options = {"OK", "Delete"};
            int choice = JOptionPane.showOptionDialog(
                    StudentManagerSystem.this,
                    sb.toString(),
                    "Student Information",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (choice == 1) { 
                Section section = sectionManager.getSectionByName(sectionName);
                if (section != null) {
                    int confirm = JOptionPane.showConfirmDialog(
                            StudentManagerSystem.this,
                            "Delete student '" + s.getName() + "'?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        section.removeStudent(s);
                        dataChanged();
                        sectionList.setSelectedValue(sectionName, true);
                    }
                }
            }
        }

        private void showAddStudentDialog(Section section) {
            JTextField nameField = new JTextField(15);
            JTextField courseField = new JTextField(15);
            JTextField gradeField = new JTextField(5);

            JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Course:"));
            panel.add(courseField);
            panel.add(new JLabel("Grade:"));
            panel.add(gradeField);

            int result = JOptionPane.showConfirmDialog(StudentManagerSystem.this,
                    panel, "Add Student", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String course = courseField.getText().trim();
                String gradeText = gradeField.getText().trim();

                if (name.isEmpty() || course.isEmpty() || gradeText.isEmpty()) {
                    JOptionPane.showMessageDialog(StudentManagerSystem.this,
                            "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    double grade = Double.parseDouble(gradeText);
                    if (grade < 0 || grade > 100) {
                        JOptionPane.showMessageDialog(StudentManagerSystem.this,
                                "Grade must be between 0 and 100.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    section.addStudent(new Student(name, course, grade));
                    dataChanged();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(StudentManagerSystem.this,
                            "Invalid grade.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class StudentListPanel extends JPanel {
        private DefaultTableModel model;
        private JTable table;

        StudentListPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(15, 15, 15, 15));
            setBackground(lightBackground);

            JLabel label = new JLabel("All Students (All Sections)");
            label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));

            String[] columns = {"Name", "Course", "Grade", "Section"};
            model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setRowHeight(24);
            table.setAutoCreateRowSorter(true);

            JScrollPane scrollPane = new JScrollPane(table);

            JButton refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(e -> refreshData());

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(lightBackground);
            topPanel.add(label, BorderLayout.WEST);
            topPanel.add(refreshButton, BorderLayout.EAST);

            add(topPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        void refreshData() {
            model.setRowCount(0);
            for (Section section : sectionManager.getSections()) {
                for (Student s : section.getStudents()) {
                    model.addRow(new Object[]{
                            s.getName(),
                            s.getCourse(),
                            s.getAverageGrade(),
                            section.getSectionName()
                    });
                }
            }
        }
    }

    private class SectionsInfoPanel extends JPanel {
        private DefaultTableModel model;
        private JTable table;

        SectionsInfoPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(new EmptyBorder(15, 15, 15, 15));
            setBackground(lightBackground);

            JLabel label = new JLabel("Sections Information");
            label.setFont(label.getFont().deriveFont(Font.BOLD, 16f));

            String[] columns = {
                    "Section",
                    "Students",
                    "Mean Grade",
                    "Median Grade",
                    "Mode Grade",
                    "Top Student",
                    "Top Grade",
                    "Lowest Student",
                    "Lowest Grade"
            };

            model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setRowHeight(24);
            table.setAutoCreateRowSorter(true);

            JScrollPane scrollPane = new JScrollPane(table);

            JButton refreshButton = new JButton("Refresh");
            refreshButton.addActionListener(e -> refreshData());

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setBackground(lightBackground);
            topPanel.add(label, BorderLayout.WEST);
            topPanel.add(refreshButton, BorderLayout.EAST);

            add(topPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);
        }

        void refreshData() {
            model.setRowCount(0);
            for (Section section : sectionManager.getSections()) {
                int count = section.getStudents().size();

                double mean = section.getMeanGrade();
                double median = section.getMedianGrade();
                Double mode = section.getModeGrade();

                Student top = section.getTopStudent();
                Student low = section.getLowestStudent();

                String meanStr = (Double.isNaN(mean)) ? "-" : String.format("%.2f", mean);
                String medianStr = (Double.isNaN(median)) ? "-" : String.format("%.2f", median);
                String modeStr = (mode == null) ? "-" : String.format("%.2f", mode);

                String topName = (top == null) ? "-" : top.getName();
                String topGradeStr = (top == null) ? "-" : String.format("%.2f", top.getAverageGrade());

                String lowName = (low == null) ? "-" : low.getName();
                String lowGradeStr = (low == null) ? "-" : String.format("%.2f", low.getAverageGrade());

                model.addRow(new Object[]{
                        section.getSectionName(),
                        count,
                        meanStr,
                        medianStr,
                        modeStr,
                        topName,
                        topGradeStr,
                        lowName,
                        lowGradeStr
                });
            }
        }
    }

    public static void main(String[] args) {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            UIManager.put("control", new Color(232, 242, 254));
            UIManager.put("nimbusBase", new Color(18, 64, 118));
            UIManager.put("nimbusBlueGrey", new Color(79, 112, 156));
            UIManager.put("nimbusLightBackground", new Color(232, 242, 254));
        } catch (Exception e) {

        }

        SwingUtilities.invokeLater(() -> {
            new StudentManagerSystem().setVisible(true);
        });
    }
}