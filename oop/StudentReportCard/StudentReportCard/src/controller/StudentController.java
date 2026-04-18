package controller;

import entity.Student;
import gui.Router;
import gui.view.DashboardView;

import javax.swing.*;

public class StudentController {
    private final DashboardView.StudentDashboardView view;
    private final Router router;
    private Student current;

    public StudentController(DashboardView.StudentDashboardView view, Router router) {
        this.view = view;
        this.router = router;
        wireCommon();
    }

    public void onLoggedIn(Student student) {
        this.current = student;
        view.bindStudent(student);

        view.viewGradesButton().addActionListener(e ->
                DashboardView.showGradesDialog(view, student));

        view.viewAttendanceButton().addActionListener(e ->
                DashboardView.showAttendanceDialog(view, student));

        view.generateReportButton().addActionListener(e -> {
            JTextArea area = new JTextArea(student.generateReport(), 25, 60);
            area.setEditable(false);
            JOptionPane.showMessageDialog(view, new JScrollPane(area),
                    "Student Report Card", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void wireCommon() {
        view.logoutButton().addActionListener(e -> {
            current = null;
            router.show("LOGIN");
        });
    }
}
