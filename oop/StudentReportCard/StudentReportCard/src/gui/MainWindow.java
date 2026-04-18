package gui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public final CardLayout cardLayout = new CardLayout();
    public final JPanel mainPanel = new JPanel(cardLayout);

    public MainWindow() {
        setTitle("Student Report Card System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        add(mainPanel);
    }
}
