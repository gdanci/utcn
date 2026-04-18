package controller;

import entity.Attendance;
import entity.Grade;
import entity.Student;
import entity.Teacher;
import gui.Router;
import gui.view.DashboardView;
import manager.StudentManager;
import manager.TeacherManager;

import javax.swing.*;
import java.util.Optional;

public class TeacherController {
    private final DashboardView.TeacherDashboardView view;
    private final Router router;
    private final StudentManager studentManager;
    private final TeacherManager teacherManager;
    private Teacher current;

    public TeacherController(DashboardView.TeacherDashboardView view, Router router, StudentManager studentManager, TeacherManager teacherManager) {
        this.view = view;
        this.router = router;
        this.studentManager = studentManager;
        this.teacherManager = teacherManager;
        wireCommon();
    }

    public void onLoggedIn(Teacher teacher) {
        this.current = teacher;
        view.bindTeacher(teacher);

        view.viewStudentsButton().addActionListener(e ->
                DashboardView.showStudentsDialog(view, teacher));

        view.addGradeButton().addActionListener(e ->
                DashboardView.showAddGradeDialog(view, teacher, (Student s, Grade g) -> {
                    boolean ok = studentManager.addGradeFor(s, teacher, g);
                    JOptionPane.showMessageDialog(view,
                            ok ? "Grade added successfully!" : "Failed to insert grade in DB.",
                            ok ? "Success" : "Error",
                            ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                }));

        view.addAttendanceButton().addActionListener(e ->
                DashboardView.showAddAttendanceDialog(view, teacher, (Student s, Attendance a) -> {
                    boolean ok = studentManager.addAttendanceFor(s, teacher, a);
                    JOptionPane.showMessageDialog(view,
                            ok ? "Attendance recorded successfully!" : "Failed to insert attendance in DB.",
                            ok ? "Success" : "Error",
                            ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
                }));

        view.classReportButton().addActionListener(e ->
                showClassReport());
    }

    private void wireCommon() {
        view.logoutButton().addActionListener(e -> {
            current = null;
            router.show("LOGIN");
        });
    }

    private void showClassReport() {
        if (current == null) return;
        JTextArea textArea = new JTextArea(current.generateReport(), 25, 60);
        textArea.setEditable(false);
        JOptionPane.showMessageDialog(view, new JScrollPane(textArea),
                "Class Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void reloadTeacher() {
        if (current == null) return;
        Optional<Teacher> reloaded = teacherManager.reloadByUserId(current.getUserId());
        reloaded.ifPresent(t -> {
            current = t;
            view.bindTeacher(t);
        });
    }
}
