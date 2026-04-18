package test;

import repository.StudentRepository;
import entity.Student;

import java.util.Optional;

public class StudentRepositoryTest {

    public static void main(String[] args) {
        System.out.println("=== StudentRepository Test ===");
        StudentRepository repo = new StudentRepository();

        int count = repo.count();
        System.out.println("Student count = " + count);

        Optional<Student> opt = repo.findByUsername("student1");

        if (opt.isPresent()) {
            Student s = opt.get();
            System.out.println("✅ Found student: " + s.getFirstName() + " " + s.getLastName());
            System.out.println("GPA: " + s.getGpa());
            System.out.println("Grades loaded: " + s.getGrades().size());
            System.out.println("Attendance records: " + s.getAttendanceRecords().size());
        } else {
            System.out.println("❌ Student not found");
        }
    }
}
