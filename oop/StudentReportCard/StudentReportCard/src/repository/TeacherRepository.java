package repository;

import entity.Attendance;
import entity.Grade;
import entity.Student;
import entity.Teacher;
import util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherRepository {
    private static final String baseSelect =
            "SELECT u.user_id, u.username, u.password, u.first_name, u.last_name, u.email, " +
                    "       t.teacher_id, t.teacher_number, t.subject, t.department " +
                    "FROM users u " +
                    "JOIN teachers t ON u.user_id = t.user_id ";

    public Optional<Teacher> findById(int userId) {
        String sql = baseSelect + "WHERE u.user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Teacher teacher = mapRowToTeacher(rs);
                    int teacherId = rs.getInt("teacher_id");
                    // Eager-load assigned students (and their grades & attendance)
                    for (Student s : loadAssignedStudents(conn, teacherId)) {
                        teacher.assignStudent(s);
                    }
                    return Optional.of(teacher);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher by id: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Teacher> findByUsername(String username) {
        String sql = baseSelect + "WHERE u.username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Teacher teacher = mapRowToTeacher(rs);
                    int teacherId = rs.getInt("teacher_id");
                    for (Student s : loadAssignedStudents(conn, teacherId)) {
                        teacher.assignStudent(s);
                    }
                    return Optional.of(teacher);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding teacher by username: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Authenticate teacher (and load their assigned students with data)
    public Optional<Teacher> authenticate(String username, String password) {
        String sql = baseSelect +
                "WHERE u.username = ? AND u.password = ? AND u.user_type = 'TEACHER'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password); // TODO: hashed compare in prod

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Teacher teacher = mapRowToTeacher(rs);
                    int teacherId = rs.getInt("teacher_id");
                    for (Student s : loadAssignedStudents(conn, teacherId)) {
                        teacher.assignStudent(s);
                    }
                    return Optional.of(teacher);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating teacher: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Teacher mapRowToTeacher(ResultSet rs) throws SQLException {
        return new Teacher(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("teacher_number"),
                rs.getString("subject"),
                rs.getString("department")
        );
    }

    private List<Student> loadAssignedStudents(Connection conn, int teacherId) throws SQLException {
        String sql =
                "SELECT s.student_id " +
                        "FROM teacher_student_assignments tsa " +
                        "JOIN students s ON s.student_id = tsa.student_id " +
                        "WHERE tsa.teacher_id = ?";

        List<Student> students = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    Student s = loadStudentByStudentId(conn, studentId);
                    if (s != null) {
                        // Eager-load this student's grades and attendance too
                        loadGradesForStudent(conn, s, studentId);
                        loadAttendanceForStudent(conn, s, studentId);
                        students.add(s);
                    }
                }
            }
        }
        return students;
    }

    private Student loadStudentByStudentId(Connection conn, int studentId) throws SQLException {
        String sql =
                "SELECT u.user_id, u.username, u.password, u.first_name, u.last_name, u.email, " +
                        "       s.student_id, s.student_number, s.class_level, s.program, s.gpa " +
                        "FROM students s " +
                        "JOIN users u ON u.user_id = s.user_id " +
                        "WHERE s.student_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("first_name"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("student_number"),
                            rs.getString("class_level"),
                            rs.getString("program")
                    );
                }
            }
        }
        return null;
    }

    private void loadGradesForStudent(Connection conn, Student student, int studentId) throws SQLException {
        String sql = "SELECT grade_id, subject, mark, grade_date, comments " +
                "FROM grades WHERE student_id = ? " +
                "ORDER BY grade_date DESC NULLS LAST, grade_id DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int gradeId = rs.getInt("grade_id");
                    String subject = rs.getString("subject");
                    double mark = rs.getDouble("mark");
                    java.sql.Date d = rs.getDate("grade_date");
                    java.time.LocalDate date = (d != null) ? d.toLocalDate() : null;
                    String comments = rs.getString("comments");

                    Grade g = new Grade(gradeId, subject, mark, date, comments);
                    student.addGrade(g);
                }
            }
        }
    }

    private void loadAttendanceForStudent(Connection conn, Student student, int studentId) throws SQLException {
        String sql = "SELECT attendance_id, attendance_date, present, status, notes " +
                "FROM attendance WHERE student_id = ? " +
                "ORDER BY attendance_date DESC NULLS LAST, attendance_id DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int attendanceId = rs.getInt("attendance_id");
                    java.sql.Date d = rs.getDate("attendance_date");
                    java.time.LocalDate date = (d != null) ? d.toLocalDate() : null;

                    boolean present = rs.getBoolean("present");
                    boolean presentWasNull = rs.wasNull();
                    if (presentWasNull) present = false;

                    String status = rs.getString("status");
                    if (status == null || status.isBlank()) {
                        status = present ? "PRESENT" : "ABSENT";
                    }

                    String notes = rs.getString("notes");

                    Attendance a = new Attendance(attendanceId, date, present, status, notes);
                    student.addAttendance(a);
                }
            }
        }
    }
}