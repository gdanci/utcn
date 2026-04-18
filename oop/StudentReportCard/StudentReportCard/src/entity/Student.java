
package entity;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {

    private String studentId;
    private List<Grade> grades;
    private List<Attendance> attendanceRecords;
    private double gpa;
    private String classLevel;
    private String program;

    public Student(int userId, String username, String password, String firstName,
                   String lastName, String email, String studentId, String classLevel, String program) {

        super(userId, username, password, firstName, lastName, email, "STUDENT");
        this.studentId = studentId;
        this.classLevel = classLevel;        // <-- assign it (you missed this before)
        this.program = program;
        this.grades = new ArrayList<>();
        this.attendanceRecords = new ArrayList<>();
        this.gpa = 0.0;
    }

    public Student(String username, String password, String firstName, String lastName, String email,
                   String studentId, String classLevel, String program) {

        this(0, username, password, firstName, lastName, email, studentId, classLevel, program);
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Student Id cannot be empty");
        }
        this.studentId = studentId;
    }

    public List<Grade> getGrades() {
        return new ArrayList<>(grades);
    }

    public List<Attendance> getAttendanceRecords() {
        return new ArrayList<>(attendanceRecords);
    }

    public double getGpa() {
        return gpa;
    }

    public String getProgram() {
        return program;
    }

    public String getClassLevel() {
        return classLevel;
    }

    public void setClassLevel(String classLevel) {
        this.classLevel = classLevel;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    @Override
    public void DisplayDashboard() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       STUDENT DASHBOARD                ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Welcome, " + getFullName() + "!");
        System.out.println("Student Number: " + studentId);
        System.out.println("Program: " + program);
        System.out.println("Class Level: " + classLevel);
        System.out.println("Current GPA: " + String.format("%.2f", gpa));
        System.out.println("\nOptions:");
        System.out.println("1. View Grades");
        System.out.println("2. View Attendance");
        System.out.println("3. Generate Report Card");
        System.out.println("4. Logout");
    }

    @Override
    public String getUserInfo() {
        return String.format("Student: %s | ID: %s | Program: %s | GPA: %.2f",
                getFullName(), studentId, program, gpa);
    }

    public void addGrade(Grade grade) {
        if (grade == null) {
            throw new IllegalArgumentException("Grade cannot be null");
        }
        grades.add(grade);
        calculateGPA();
    }

    public boolean removeGrade(int gradeId) {
        for (int i = 0; i < grades.size(); i++) {
            Grade g = grades.get(i);
            if (g.getGradeId() == gradeId) {
                grades.remove(i);
                calculateGPA();
                return true;
            }
        }
        return false;
    }

    public void calculateGPA() {
        if (grades.isEmpty()) {
            this.gpa = 0.0;
            return;
        }
        double totalMarks = 0.0;
        for (Grade g : grades) {
            totalMarks += g.getMark();
        }
        this.gpa = totalMarks / grades.size();
    }

    public void viewGrades() {
        if (grades.isEmpty()) {
            System.out.println("No grades.");
            return;
        }
        System.out.println("\n=== Grades for " + getFullName() + " ===");
        System.out.println("─────────────────────────────────────────");
        for (Grade g : grades) {
            System.out.println(g);
        }
        System.out.println("─────────────────────────────────────────");
        System.out.println("Current GPA: " + String.format("%.2f", gpa));
    }

    public void addAttendance(Attendance attendance) {
        if (attendance == null) {
            throw new IllegalArgumentException("Attendance cannot be null");
        }
        attendanceRecords.add(attendance);
    }

    public void viewAttendance() {
        if (attendanceRecords.isEmpty()) {
            System.out.println("No attendance records yet.");
            return;
        }

        System.out.println("\n=== Attendance for " + getFullName() + " ===");
        System.out.println("─────────────────────────────────────────");

        int presentCount = 0;
        for (Attendance a : attendanceRecords) {
            System.out.println(a);
            if (a.isPresent()) {
                presentCount++;
            }
        }

        double attendanceRate = (presentCount * 100.0) / attendanceRecords.size();
        System.out.println("─────────────────────────────────────────");
        System.out.println("Attendance Rate: " + String.format("%.1f%%", attendanceRate));
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n╔════════════════════════════════════════╗\n");
        report.append("║         STUDENT REPORT CARD            ║\n");
        report.append("╚════════════════════════════════════════╝\n\n");
        report.append("Student Name: ").append(getFullName()).append("\n");
        report.append("Student Number: ").append(studentId).append("\n");
        report.append("Program: ").append(program).append("\n");
        report.append("Class Level: ").append(classLevel).append("\n");
        report.append("Email: ").append(getEmail()).append("\n\n");


        report.append("─── Academic Performance ───\n");
        if (grades.isEmpty()) {
            report.append("No grades recorded.\n");
        } else {
            for (Grade g : grades) {
                report.append(g.toString()).append("\n");
            }
            report.append("\nOverall GPA: ").append(String.format("%.2f", gpa)).append("\n");
        }

        report.append("\n─── Attendance Summary ───\n");
        if (attendanceRecords.isEmpty()) {
            report.append("No attendance records.\n");
        } else {
            int presentCount = 0;
            for (Attendance a : attendanceRecords) {
                if (a.isPresent()) presentCount++;
            }
            double attendanceRate = (presentCount * 100.0) / attendanceRecords.size();
            report.append("Total Classes: ").append(attendanceRecords.size()).append("\n");
            report.append("Classes Attended: ").append(presentCount).append("\n");
            report.append("Attendance Rate: ").append(String.format("%.1f%%", attendanceRate)).append("\n");
        }

        return report.toString();
    }

    public void viewReport() {
        System.out.println(generateReport());
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentNumber='" + studentId + '\'' +
                ", name='" + getFullName() + '\'' +
                ", program='" + program + '\'' +
                ", classLevel='" + classLevel + '\'' +
                ", gpa=" + String.format("%.2f", gpa) +
                ", gradesCount=" + grades.size() +
                '}';
    }
}



