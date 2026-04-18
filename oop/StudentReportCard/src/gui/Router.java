package gui;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Router {
    private final MainWindow window;
    private final Map<String, JComponent> screens = new HashMap<>();

    public Router(MainWindow window) {
        this.window = window;
    }

    public void register(String name, JComponent view) {
        if (!screens.containsKey(name)) {
            screens.put(name, view);
            window.mainPanel.add(view, name);
        }
    }

    public void show(String name) {
        window.cardLayout.show(window.mainPanel, name);
    }
}
