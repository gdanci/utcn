package manager;

import entity.*;
import repository.StudentRepository;

public class StudentManager {
    private final StudentRepository studentRepo;
    public StudentManager(StudentRepository repo) { this.studentRepo = repo; }

    public boolean addGradeFor(Student s, Teacher t, Grade g) { return studentRepo.addGradeFor(s, t, g); }
    public boolean addAttendanceFor(Student s, Teacher t, Attendance a) { return studentRepo.addAttendanceFor(s, t, a); }
}
