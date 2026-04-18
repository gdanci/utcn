package controller;

import entity.Student;
import entity.Teacher;
import gui.Router;
import gui.view.LoginView;
import manager.AuthManager;

public class LoginController {
    private final LoginView view;
    private final Router router;
    private final AuthManager auth;
    private final StudentController studentController;
    private final TeacherController teacherController;

    public LoginController(LoginView v, Router r, AuthManager a, StudentController studentCtrl,
                           TeacherController teacherCtrl) {
        this.view = v;
        this.router = r;
        this.auth = a;
        this.studentController = studentCtrl;
        this.teacherController = teacherCtrl;
        wire();
    }

    private void wire() {
        view.getLoginButton().addActionListener(e -> {
            var username = view.getUsername();
            var password = view.getPassword();
            var asStudent = view.isStudentSelected();

            if (username.isBlank() || password.isBlank()) {
                view.setError("Please enter username and password");
                return;
            }

            var userOpt = auth.login(username, password, asStudent);
            if (userOpt.isEmpty()) {
                view.setError("Invalid username or password");
                view.clear();
                return;
            }

            view.clear();

            if (asStudent) {
                var student = (Student) userOpt.get();
                // 👉 EXACT SPOT TO WIRE:
                studentController.onLoggedIn(student);
                router.show("STUDENT");
            } else {
                var teacher = (Teacher) userOpt.get();
                teacherController.onLoggedIn(teacher);
                router.show("TEACHER");
            }
        });
    }
}
