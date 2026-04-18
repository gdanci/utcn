package gui.view;

import entity.Attendance;
import entity.Grade;
import entity.Student;
import entity.Teacher;
import entity.User;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.function.BiConsumer;

public class DashboardView {
    public static class StudentDashboardView extends JPanel {
        private final JLabel header = new JLabel("", SwingConstants.CENTER);
        private final JLabel info = new JLabel("", SwingConstants.LEFT);

        private final JButton viewGradesBtn = new JButton("View Grades");
        private final JButton viewAttendanceBtn = new JButton("View Attendance");
        private final JButton generateReportBtn = new JButton("Generate Report");
        private final JButton logoutBtn = new JButton("Logout");

        private Student current;

        public StudentDashboardView() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            // Header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(255, 182, 193));
            header.setFont(new Font("Times New Roman", Font.BOLD, 20));
            header.setForeground(Color.WHITE);
            headerPanel.add(header);
            add(headerPanel, BorderLayout.NORTH);

            // Info
            JPanel center = new JPanel(new GridLayout(1, 1, 10, 10));
            center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            center.setBackground(Color.WHITE);
            info.setFont(new Font("Arial", Font.PLAIN, 16));
            center.add(info);
            add(center, BorderLayout.CENTER);

            // Buttons
            JPanel buttons = new JPanel(new GridLayout(2, 2, 15, 15));
            buttons.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
            buttons.setBackground(Color.WHITE);

            stylePrimary(viewGradesBtn);
            stylePrimary(viewAttendanceBtn);
            stylePrimary(generateReportBtn);
            styleDanger(logoutBtn);

            buttons.add(viewGradesBtn);
            buttons.add(viewAttendanceBtn);
            buttons.add(generateReportBtn);
            buttons.add(logoutBtn);

            add(buttons, BorderLayout.SOUTH);
        }

        public void bindStudent(Student s) {
            this.current = s;
            header.setText("Student Dashboard — Welcome, " + s.getFullName());
            info.setText(String.format(
                    "<html>Student ID: %s<br/>Program: %s<br/>GPA: %.2f<br/>Total Grades: %d</html>",
                    s.getStudentId(), s.getProgram(), s.getGpa(), s.getGrades().size()
            ));
        }

        public Student getCurrent() { return current; }

        public JButton viewGradesButton() { return viewGradesBtn; }
        public JButton viewAttendanceButton() { return viewAttendanceBtn; }
        public JButton generateReportButton() { return generateReportBtn; }
        public JButton logoutButton() { return logoutBtn; }
    }

    //Teacher Dashboard
    public static class TeacherDashboardView extends JPanel {
        private final JLabel header = new JLabel("", SwingConstants.CENTER);
        private final JLabel info = new JLabel("", SwingConstants.LEFT);

        private final JButton viewStudentsBtn = new JButton("View Students");
        private final JButton addGradeBtn = new JButton("Add Grade");
        private final JButton addAttendanceBtn = new JButton("Add Attendance");
        private final JButton classReportBtn = new JButton("Class Report");
        private final JButton logoutBtn = new JButton("Logout");

        private Teacher current;

        public TeacherDashboardView() {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);

            // Header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(new Color(255, 182, 193));
            header.setFont(new Font("Arial", Font.BOLD, 20));
            header.setForeground(Color.WHITE);
            headerPanel.add(header);
            add(headerPanel, BorderLayout.NORTH);

            // Info
            JPanel center = new JPanel(new GridLayout(1, 1, 10, 10));
            center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            center.setBackground(Color.WHITE);
            info.setFont(new Font("Arial", Font.PLAIN, 16));
            center.add(info);
            add(center, BorderLayout.CENTER);

            // Buttons
            JPanel buttons = new JPanel(new GridLayout(2, 3, 15, 15));
            buttons.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
            buttons.setBackground(Color.WHITE);

            stylePrimary(viewStudentsBtn);
            stylePrimary(addGradeBtn);
            stylePrimary(addAttendanceBtn);
            stylePrimary(classReportBtn);
            styleDanger(logoutBtn);

            buttons.add(viewStudentsBtn);
            buttons.add(addGradeBtn);
            buttons.add(addAttendanceBtn);
            buttons.add(classReportBtn);
            buttons.add(new JLabel()); // filler
            buttons.add(logoutBtn);

            add(buttons, BorderLayout.SOUTH);
        }

        public void bindTeacher(Teacher t) {
            this.current = t;
            header.setText("Teacher Dashboard — Welcome, " + t.getFullName());
            info.setText(String.format(
                    "<html>Teacher#: %s<br/>Subject: %s<br/>Department: %s<br/>Assigned Students: %d</html>",
                    t.getTeacherNumber(), t.getSubject(), t.getDepartment(), t.getAssignedStudents().size()
            ));
        }

        public Teacher getCurrent() { return current; }

        public JButton viewStudentsButton() { return viewStudentsBtn; }
        public JButton addGradeButton() { return addGradeBtn; }
        public JButton addAttendanceButton() { return addAttendanceBtn; }
        public JButton classReportButton() { return classReportBtn; }
        public JButton logoutButton() { return logoutBtn; }
    }

    public static void showGradesDialog(Component parent, Student student) {
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder content = new StringBuilder();
        content.append("=== GRADES FOR ").append(student.getFullName()).append(" ===\n\n");

        if (student.getGrades().isEmpty()) {
            content.append("No grades recorded yet.");
        } else {
            for (Grade grade : student.getGrades()) {
                content.append(grade.toString()).append("\n");
            }
            content.append("\n───────────────────────────────────────\n");
            content.append("Current GPA: ").append(String.format("%.2f", student.getGpa()));
        }

        textArea.setText(content.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(parent, scrollPane, "View Grades", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showAttendanceDialog(Component parent, Student student) {
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder content = new StringBuilder();
        content.append("=== ATTENDANCE FOR ").append(student.getFullName()).append(" ===\n\n");

        List<entity.Attendance> list = student.getAttendanceRecords(); // adapt to your entity API
        if (list.isEmpty()) {
            content.append("No attendance records yet.");
        } else {
            int presentCount = 0;
            for (entity.Attendance a : list) {
                content.append(a.toString()).append("\n");
                if (a.isPresent()) presentCount++;
            }
            double rate = (presentCount * 100.0) / list.size();
            content.append("\n───────────────────────────────────────\n");
            content.append("Attendance Rate: ").append(String.format("%.1f%%", rate));
        }

        textArea.setText(content.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(parent, scrollPane, "View Attendance", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showStudentsDialog(Component parent, Teacher teacher) {
        JTextArea textArea = new JTextArea(20, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder content = new StringBuilder();
        content.append("=== STUDENTS IN ").append(teacher.getSubject()).append(" CLASS ===\n\n");

        List<Student> assigned = teacher.getAssignedStudents();
        if (assigned.isEmpty()) {
            content.append("No students assigned yet.");
        } else {
            int i = 1;
            for (Student s : assigned) {
                content.append(i++).append(". ").append(s.getUserInfo()).append("\n");
            }
        }

        textArea.setText(content.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(parent, scrollPane, "View Students", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showAddGradeDialog(Component parent, Teacher teacher, BiConsumer<Student, Grade> onSubmit) {
        List<Student> assigned = teacher.getAssignedStudents();
        if (assigned.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No students assigned to add grades for.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        String[] studentNames = assigned.stream().map(User::getFullName).toArray(String[]::new);
        JComboBox<String> studentCombo = new JComboBox<>(studentNames);

        JTextField subjectField = new JTextField();
        JTextField markField = new JTextField();
        JTextField commentsField = new JTextField();

        panel.add(new JLabel("Student:"));
        panel.add(studentCombo);
        panel.add(new JLabel("Subject:"));
        panel.add(subjectField);
        panel.add(new JLabel("Mark (0-100):"));
        panel.add(markField);
        panel.add(new JLabel("Comments:"));
        panel.add(commentsField);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Add Grade",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Student selected = assigned.get(studentCombo.getSelectedIndex());
                String subject = subjectField.getText();
                double mark = Double.parseDouble(markField.getText());
                String comments = commentsField.getText();

                Grade grade = new Grade(subject, mark, LocalDate.now(), comments);
                onSubmit.accept(selected, grade);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void showAddAttendanceDialog(Component parent,
                                               Teacher teacher,
                                               BiConsumer<Student, Attendance> onSubmit) {
        List<Student> assigned = teacher.getAssignedStudents();
        if (assigned.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "No students assigned to add attendance for.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        String[] studentNames = assigned.stream().map(User::getFullName).toArray(String[]::new);
        JComboBox<String> studentCombo = new JComboBox<>(studentNames);

        String[] statuses = {"PRESENT", "ABSENT", "LATE", "EXCUSED"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);

        panel.add(new JLabel("Student:"));
        panel.add(studentCombo);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Add Attendance",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            Student selected = assigned.get(studentCombo.getSelectedIndex());
            String status = (String) statusCombo.getSelectedItem();
            Attendance attendance = new Attendance(LocalDate.now(), status);
            onSubmit.accept(selected, attendance);
        }
    }

    private static void stylePrimary(JButton b) {
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(new Color(239, 147, 192));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(180, 50));
    }

    private static void styleDanger(JButton b) {
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setBackground(new Color(255, 109, 94));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(180, 50));
    }
}
