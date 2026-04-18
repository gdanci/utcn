package manager;

import entity.Teacher;
import repository.StudentRepository;
import repository.TeacherRepository;

import java.util.Optional;

public class TeacherManager {
    private final TeacherRepository teacherRepo;
    private final manager.StudentManager studentManager;

    public TeacherManager(TeacherRepository tRepo, StudentRepository sRepo) {
        this.teacherRepo = tRepo;
        this.studentManager = new StudentManager(sRepo);
    }

    public Optional<Teacher> reloadByUserId(int userId) { return teacherRepo.findById(userId); }
}