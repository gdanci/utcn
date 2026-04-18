
package entity;

import java.util.ArrayList;
import java.util.List;

public class Teacher extends User {
    private String teacherNumber;
    private String subject;
    private String department;
    private final List<Student> assignedStudents;

    public Teacher(int userId, String username, String password, String firstName, String lastName,
                   String email, String teacherNumber, String subject, String department) {

        super(userId, username, password, firstName, lastName, email, "TEACHER");
        if (teacherNumber == null || teacherNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher number cannot be empty");
        }
        this.teacherNumber = teacherNumber;
        this.subject = subject;
        this.department = department;
        this.assignedStudents = new ArrayList<>();
    }

    public Teacher(String username, String password, String firstName, String lastName, String email, String teacherNumber, String subject, String department) {
        this(0, username, password, firstName, lastName, email, teacherNumber, subject, department);
    }

    public String getTeacherNumber() {
        return teacherNumber;
    }

    public void setTeacherNumber(String teacherNumber) {
        if (teacherNumber == null || teacherNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Teacher number cannot be empty");
        }
        this.teacherNumber = teacherNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Student> getAssignedStudents() {
        return new ArrayList<>(assignedStudents);
    }


    @Override
    public void DisplayDashboard() {
        // Keep to satisfy your abstract method name, but delegate to the idiomatic one
        displayDashboard();
    }

    public void displayDashboard() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║       TEACHER DASHBOARD                ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Welcome, " + getFullName() + "!");
        System.out.println("Teacher Number: " + teacherNumber);
        System.out.println("Subject: " + subject);
        System.out.println("Department: " + department);
        System.out.println("Students Assigned: " + assignedStudents.size());
        System.out.println("\nOptions:");
        System.out.println("1. View All Students");
        System.out.println("2. Add Grade for Student");
        System.out.println("3. Edit Grade");
        System.out.println("4. Remove Grade");
        System.out.println("5. Add Attendance Record");
        System.out.println("6. Generate Class Report");
        System.out.println("7. Logout");
    }

    @Override
    public String getUserInfo() {
        return String.format("Teacher: %s | Number: %s | Subject: %s | Department: %s",
                getFullName(), teacherNumber, subject, department);
    }

    public void assignStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (!assignedStudents.contains(student)) {
            assignedStudents.add(student);
            System.out.println("Student " + student.getFullName() + " assigned successfully.");
        } else {
            System.out.println("Student is already assigned to this class.");
        }
    }

    public void addGradeForStudent(Student student, Grade grade) {
        if (student == null || grade == null) {
            throw new IllegalArgumentException("Student and Grade cannot be null");
        }
        if (!assignedStudents.contains(student)) {
            System.out.println("Error: Student is not assigned to this teacher.");
            return;
        }
        student.addGrade(grade);
        System.out.println("Grade added successfully for " + student.getFullName());
        System.out.println("Subject: " + grade.getSubject() + " | Mark: " + grade.getMark());
    }

    public void editGrade(Student student, int gradeId, double newMark, String newSubject) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (!assignedStudents.contains(student)) {
            System.out.println("Error: Student is not assigned to this teacher.");
            return;
        }
        List<Grade> grades = student.getGrades();
        for (Grade g : grades) {
            if (g.getGradeId() == gradeId) {
                g.setMark(newMark);
                g.setSubject(newSubject);
                student.calculateGPA();
                System.out.println("Grade updated successfully for " + student.getFullName());
                return;
            }
        }
        System.out.println("Grade not found.");
    }

    public void removeGrade(Student student, int gradeId) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        if (!assignedStudents.contains(student)) {
            System.out.println("Error: Student is not assigned to this teacher.");
            return;
        }
        if (student.removeGrade(gradeId)) {
            System.out.println("Grade removed successfully for " + student.getFullName());
        } else {
            System.out.println("Grade not found.");
        }
    }

    public void addAttendanceForStudent(Student student, Attendance attendance) {
        if (student == null || attendance == null) {
            throw new IllegalArgumentException("Student and Attendance cannot be null");
        }
        if (!assignedStudents.contains(student)) {
            System.out.println("Error: Student is not assigned to this teacher.");
            return;
        }
        student.addAttendance(attendance);
        System.out.println("Attendance recorded for " + student.getFullName());
    }

    public void viewAllStudents() {
        if (assignedStudents.isEmpty()) {
            System.out.println("No students assigned yet.");
            return;
        }
        System.out.println("\n=== Students in " + subject + " Class ===");
        System.out.println("─────────────────────────────────────────");
        for (int i = 0; i < assignedStudents.size(); i++) {
            Student s = assignedStudents.get(i);
            System.out.println((i + 1) + ". " + s.getUserInfo());
        }
        System.out.println("─────────────────────────────────────────");
        System.out.println("Total Students: " + assignedStudents.size());
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n╔════════════════════════════════════════╗\n");
        report.append("║         CLASS SUMMARY REPORT           ║\n");
        report.append("╚════════════════════════════════════════╝\n\n");
        report.append("Teacher: ").append(getFullName()).append("\n");
        report.append("Subject: ").append(subject).append("\n");
        report.append("Department: ").append(department).append("\n");
        report.append("Total Students: ").append(assignedStudents.size()).append("\n\n");

        if (assignedStudents.isEmpty()) {
            report.append("No students assigned to this class.\n");
            return report.toString();
        }

        report.append("─── Student Performance Summary ───\n\n");

        double totalClassGPA = 0.0;
        int studentsWithGrades = 0;

        for (Student s : assignedStudents) {
            report.append("• ").append(s.getFullName())
                    .append(" (").append(s.getStudentId()).append(")\n");
            report.append("  GPA: ").append(String.format("%.2f", s.getGpa()))
                    .append(" | Grades: ").append(s.getGrades().size()).append("\n");

            if (s.getGpa() > 0) {
                totalClassGPA += s.getGpa();
                studentsWithGrades++;
            }
        }

        if (studentsWithGrades > 0) {
            double classAverage = totalClassGPA / studentsWithGrades;
            report.append("\n─── Class Statistics ───\n");
            report.append("Class Average GPA: ").append(String.format("%.2f", classAverage)).append("\n");
        }

        return report.toString();
    }

    public void viewReport() {
        System.out.println(generateReport());
    }

    public Student findStudentByNumber(String studentNumber) {
        for (Student s : assignedStudents) {
            if (s.getStudentId().equals(studentNumber)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "teacherNumber='" + teacherNumber + '\'' +
                ", name='" + getFullName() + '\'' +
                ", subject='" + subject + '\'' +
                ", department='" + department + '\'' +
                ", assignedStudents=" + assignedStudents.size() +
                '}';
    }
}
