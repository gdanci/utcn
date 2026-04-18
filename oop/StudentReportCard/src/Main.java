import controller.*;
import gui.*;
import gui.view.DashboardView;
import gui.view.LoginView;
import manager.*;
import repository.*;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Window & routing
            var window = new MainWindow();
            var router = new Router(window);

            // Repositories
            var studentRepo = new StudentRepository();
            var teacherRepo = new TeacherRepository();

            // Managers / services
            var authMgr = new AuthManager(studentRepo, teacherRepo);
            var studentMgr = new StudentManager(studentRepo);
            var teacherMgr = new TeacherManager(teacherRepo, studentRepo);

            // Views
            var loginView = new LoginView();
            var studentView = new DashboardView.StudentDashboardView();
            var teacherView = new DashboardView.TeacherDashboardView();

            // Controllers
            var studentController = new StudentController(studentView, router);
            var teacherController = new TeacherController(teacherView, router, studentMgr, teacherMgr);
            new LoginController(loginView, router, authMgr, studentController, teacherController);

            // Register screens once
            router.register("LOGIN", loginView);
            router.register("STUDENT", studentView);
            router.register("TEACHER", teacherView);

            // Start UI
            router.show("LOGIN");
            window.setVisible(true);
        });
    }
}
