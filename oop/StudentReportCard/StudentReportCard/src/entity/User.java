package entity;

public abstract class User {
    private int userId;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String role;       // "STUDENT" or "TEACHER"

    public User(int userId, String username, String password, String firstName,
                String lastName, String email, String role) {
        this.userId = userId;
        setUsername(username);
        setPassword(password);
        this.firstName = firstName;
        this.lastName = lastName;
        setEmail(email);
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        this.password = password;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format!");
        }
        this.email = email;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public abstract void DisplayDashboard();

    public abstract String getUserInfo();

    public String getFullName() {
        // Avoid trailing spaces if one of the names is null/empty
        String fn = firstName == null ? "" : firstName.trim();
        String ln = lastName == null ? "" : lastName.trim();
        return (fn + " " + ln).trim();
    }

    public boolean checkPassword(String inputpassword) {
        // For production: use BCrypt.checkpw(inputpassword, this.passwordHash)
        return this.password != null && this.password.equals(inputpassword);
    }

    @Override
    public String toString() {
        return "User{ " +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", name='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                " }";
    }
}
