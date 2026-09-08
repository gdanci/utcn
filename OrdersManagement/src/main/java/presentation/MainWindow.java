package presentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * The main dashboard of the application.
 * Provides navigation buttons to access Client, Product, and Order management windows.
 * @author Gabriella Danci
 */
public class MainWindow extends JFrame {

    /**
     * Constructs the main window and initializes the navigation buttons.
     */
    public MainWindow() {
        setTitle("Order Management System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(200, 120));

        JButton clientsBtn  = new JButton("Clients");
        JButton productsBtn = new JButton("Products");
        JButton ordersBtn   = new JButton("Orders");

        clientsBtn.addActionListener(e  -> openWindow(new ClientWindow()));
        productsBtn.addActionListener(e -> openWindow(new ProductWindow()));
        ordersBtn.addActionListener(e   -> openWindow(new OrderWindow()));

        panel.add(clientsBtn);
        panel.add(productsBtn);
        panel.add(ordersBtn);

        add(panel);
        setVisible(true);
    }

    /**
     * Opens another window and hides the main menu until the new window is closed.
     * @param window The JFrame to be displayed.
     */
    private void openWindow(JFrame window) {
        setVisible(false);
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                setVisible(true);
            }
        });
    }

    /**
     * Application entry point.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainWindow::new);
    }
}