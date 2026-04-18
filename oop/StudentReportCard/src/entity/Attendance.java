package entity;

import java.time.LocalDate;

public class Attendance {

    private int attendanceId;
    private LocalDate date;
    private boolean present;
    private String status; // "PRESENT", "ABSENT", "LATE", "EXCUSED"
    private String notes;

    public Attendance(int attendanceId, LocalDate date, boolean present, String status, String notes) {
        this.attendanceId = attendanceId;
        this.date = date;
        this.present = present;
        setStatus(status);
        this.notes = notes;
    }

    public Attendance(LocalDate date, boolean present, String status, String notes) {
        this(0, date, present, status, notes);
    }

    public Attendance(LocalDate date, boolean present) {
        this(0, date, present, present ? "PRESENT" : "ABSENT", "");
    }

    public Attendance(LocalDate date, String status) {
        this(0, date, status.equals("PRESENT"), status, "");
    }

    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }
        this.date = date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
        // Auto-update status based on present flag
        if (present && (status.equals("ABSENT") || status.equals("LATE"))) {
            this.status = "PRESENT";
        } else if (!present && status.equals("PRESENT")) {
            this.status = "ABSENT";
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }

        // Validate status
        String upperStatus = status.toUpperCase();
        if (!upperStatus.equals("PRESENT") && !upperStatus.equals("ABSENT") &&
                !upperStatus.equals("LATE") && !upperStatus.equals("EXCUSED")) {
            throw new IllegalArgumentException("Invalid status. Must be PRESENT, ABSENT, LATE, or EXCUSED");
        }

        this.status = upperStatus;
        // Update present flag based on status
        this.present = upperStatus.equals("PRESENT") || upperStatus.equals("LATE");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isExcused() {
        return status.equals("EXCUSED");
    }

    public boolean isLate() {
        return status.equals("LATE");
    }

    public String getStatusSymbol() {
        switch (status) {
            case "PRESENT":
                return "✓";
            case "ABSENT":
                return "✗";
            case "LATE":
                return "⏰";
            case "EXCUSED":
                return "E";
            default:
                return "?";
        }
    }

    @Override
    public String toString() {
        return String.format("%s [%s] %s - %s%s",
                getStatusSymbol(),
                date,
                status,
                present ? "Present" : "Absent",
                notes.isEmpty() ? "" : " | " + notes);
    }

    public String getDetailedInfo() {
        return String.format(
                "Date: %s\n" +
                        "Status: %s (%s)\n" +
                        "Present: %s\n" +
                        "Notes: %s",
                date,
                status,
                getStatusSymbol(),
                present ? "Yes" : "No",
                notes.isEmpty() ? "None" : notes
        );
    }
}
