package gui.view;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel {
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JRadioButton studentRadio = new JRadioButton("Student", true);
    private final JRadioButton teacherRadio = new JRadioButton("Teacher");
    private final JButton loginButton = new JButton("Login");
    private final JLabel errorLabel = new JLabel("");

    public LoginView() {
        setLayout(new GridBagLayout());
        setBackground(new Color(240,240,245));
        var gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);

        var title = new JLabel("Student Report Card System");
        title.setFont(new Font("Times New Roman", Font.BOLD, 26));
        title.setForeground(new Color(255,182,193));

        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; add(title, gbc);

        gbc.gridwidth=1; gbc.gridy=1; gbc.gridx=0; add(new JLabel("Username:"), gbc);
        gbc.gridx=1; add(usernameField, gbc);

        gbc.gridy=2; gbc.gridx=0; add(new JLabel("Password:"), gbc);
        gbc.gridx=1; add(passwordField, gbc);

        gbc.gridy=3; gbc.gridx=0; add(new JLabel("Login as:"), gbc);
        var rolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        var group = new ButtonGroup();
        group.add(studentRadio); group.add(teacherRadio);
        rolePanel.setBackground(getBackground());
        rolePanel.add(studentRadio); rolePanel.add(teacherRadio);
        gbc.gridx=1; add(rolePanel, gbc);

        gbc.gridy=4; gbc.gridx=0; gbc.gridwidth=2;
        loginButton.setPreferredSize(new Dimension(150,40));
        loginButton.setBackground(new Color(239, 139, 192));
        loginButton.setForeground(Color.WHITE); // different from bg
        add(loginButton, gbc);

        gbc.gridy=5; add(errorLabel, gbc);
    }

    public String getUsername() { return usernameField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public boolean isStudentSelected() { return studentRadio.isSelected(); }
    public JButton getLoginButton() { return loginButton; }
    public void setError(String msg) { errorLabel.setText(msg); }
    public void clear() { passwordField.setText(""); setError(""); }
}
