package manager;

import repository.StudentRepository;
import repository.TeacherRepository;

import java.util.Optional;

public class AuthManager {
    private final StudentRepository studentRepo;
    private final TeacherRepository teacherRepo;

    public AuthManager(StudentRepository s, TeacherRepository t) {
        this.studentRepo = s; this.teacherRepo = t;
    }

    public Optional<? extends entity.User> login(String username, String password, boolean asStudent) {
        if (asStudent) return studentRepo.authenticate(username, password);
        return teacherRepo.authenticate(username, password);
    }
}

