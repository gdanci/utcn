package repository;

import entity.*;
import util.DatabaseManager;
import java.sql.*;
import java.util.Optional;

public class StudentRepository {
    String baseSelect = "SELECT u.user_id, u.username, u.password, u.first_name, u.last_name, u.email, " +
            "       s.student_id, s.student_number, s.class_level, s.program, s.gpa " +
            "FROM users u JOIN students s ON u.user_id = s.user_id ";

    public Optional<Student> findById(int userId) {
        String sql = baseSelect + "WHERE u.user_id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = mapResultSetToStudent(rs);
                    int studentId = rs.getInt("student_id");

                    loadGradesForStudent(conn, s, studentId);
                    loadAttendanceForStudent(conn, s, studentId);

                    return Optional.of(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding student by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Student> findByUsername(String username) {
        String sql = baseSelect + "WHERE u.username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = mapResultSetToStudent(rs);
                    int studentId = rs.getInt("student_id");

                    loadGradesForStudent(conn, s, studentId);
                    loadAttendanceForStudent(conn, s, studentId);

                    return Optional.of(s);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error finding student by username: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Student> authenticate(String username, String password) {
        String sql = baseSelect +
                "WHERE u.username = ? AND u.password = ? AND u.user_type = 'STUDENT'";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password); // TODO hash

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Student s = mapResultSetToStudent(rs);
                    int studentId = rs.getInt("student_id");
                    loadGradesForStudent(conn, s, studentId);
                    loadAttendanceForStudent(conn, s, studentId);
                    return Optional.of(s);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public int count() {
        String sql = "SELECT COUNT(*) FROM students";

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error counting students: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Insert grade for a student by student.user_id and teacher.user_id
    public boolean addGradeFor(Student student, Teacher teacher, Grade grade) {
        String sql =
                "INSERT INTO grades (student_id, subject, mark, grade_date, comments, teacher_id) " +
                        "VALUES ( (SELECT s.student_id FROM students s WHERE s.user_id = ?), ?, ?, ?, ?, " +
                        "         (SELECT t.teacher_id FROM teachers t WHERE t.user_id = ?) )";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, student.getUserId());
            ps.setString(2, grade.getSubject());
            ps.setDouble(3, grade.getMark());
            ps.setObject(4, grade.getDate());
            ps.setString(5, grade.getComments());
            ps.setInt(6, teacher.getUserId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error inserting grade: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean addAttendanceFor(Student student, Teacher teacher, Attendance attendance) {
        String sql =
                "INSERT INTO attendance (student_id, attendance_date, present, status, notes, recorded_by) " +
                        "VALUES ( " +
                        "  (SELECT s.student_id FROM students s WHERE s.user_id = ?), " +
                        "  ?, ?, ?::attendance_status_enum, ?, " +
                        "  (SELECT t.teacher_id FROM teachers t WHERE t.user_id = ?) " +
                        ")";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, student.getUserId());
            ps.setObject(2, attendance.getDate());
            ps.setBoolean(3, attendance.isPresent());
            ps.setString(4, attendance.getStatus());
            ps.setString(5, attendance.getNotes());
            ps.setInt(6, teacher.getUserId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Error inserting attendance: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student(
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
        return student;
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

                    if (presentWasNull) {
                        present = false;
                    }

                    String status = rs.getString("status");
                    String notes = rs.getString("notes");

                    Attendance a = new Attendance(attendanceId, date, present, status, notes);

                    student.addAttendance(a);
                }
            }
        }
    }
}
