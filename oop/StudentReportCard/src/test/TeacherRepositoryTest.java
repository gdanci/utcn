
package test;

import repository.TeacherRepository;
import entity.Teacher;

import java.util.Optional;

public class TeacherRepositoryTest {

    public static void main(String[] args) {
        System.out.println("=== TeacherRepository Test ===");
        TeacherRepository repo = new TeacherRepository();

        Optional<Teacher> opt = repo.findByUsername("teacher1");

        if (opt.isPresent()) {
            Teacher t = opt.get();
            System.out.println("✅ Found teacher: " + t.getFirstName());
            System.out.println("Assigned students: " + t.getAssignedStudents().size());

            t.getAssignedStudents().forEach(s ->
                    System.out.println("- " + s.getFirstName() +
                            " (grades=" + s.getGrades().size() + ")")
            );
        } else {
            System.out.println("❌ Teacher not found");
        }
    }
}
