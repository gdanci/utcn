package entity;

import java.time.LocalDate;

public class Grade {

    private int gradeId;
    private String subject;
    private double mark;
    private LocalDate date;
    private String comments;

    public Grade(int gradeId, String subject, double mark, LocalDate date, String comments) {
        this.gradeId = gradeId;
        setSubject(subject);
        setMark(mark);
        this.date = date;
        this.comments = comments;
    }

    public Grade(String subject, double mark, LocalDate date, String comments) {
        this(0, subject, mark, date, comments);
    }

    public Grade(String subject, double mark) {
        this(0, subject, mark, LocalDate.now(), "");
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId){
        this.gradeId = gradeId;
    }

    public String getSubject() {
        return subject;
    }

    public double getMark() {
        return mark;
    }

    public void setSubject(String subject) {
        if(subject == null || subject.trim().isEmpty()){
            throw new IllegalArgumentException("Subject cannot be empty");
        }
        this.subject = subject;
    }

    public void setMark(double mark){
        if(mark < 0 || mark > 100){
            throw new IllegalArgumentException("Mark must be between 0 and 100");
        }
        this.mark = mark;
    }

    public LocalDate getDate(){
        return date;
    }

    public void setDate(LocalDate date){
        this.date = date;
    }

    public String getComments(){
        return comments;
    }

    public void setComments(String comments){
        this.comments = comments;
    }

    public boolean isPassing(){
        return mark >= 50;
    }

    @Override
    public String toString() {
        return String.format("%s: %.1f%% - %s%s", subject, mark, date, comments.isEmpty() ? "" : " | " + comments);
    }

    public String getDetailedInfo() {
        return String.format("Subject: %s\n" + "Mark: %.1f%%\n" + "Date: %s\n" + "Status: %s\n" + "Comments: %s",
                subject, mark, date, isPassing() ? "PASS" : "FAIL", comments.isEmpty() ? "None" : comments);
    }
}
