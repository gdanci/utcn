package gui;

import business.*;
import strategy.*;
import model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class SimulationFrame extends JFrame {
    private SimulationManager manager;

    private JTextField tfClients;
    private JTextField tfQueues;
    private JTextField tfTimeLimit;
    private JTextField tfMinArrival;
    private JTextField tfMaxArrival;
    private JTextField tfMinService;
    private JTextField tfMaxService;
    private JComboBox<SelectionPolicy> cbPolicy;

    private JLabel lblCurrentTime;
    private JTextArea taWaitingClients;
    private JPanel queuesPanel;

    private JLabel lblAvgWaiting;
    private JLabel lblAvgService;
    private JLabel lblPeakHour;

    private JButton btnStart;

    public SimulationFrame(SimulationManager manager) {
        this.manager = manager;
        setTitle("Queue Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildSetupPanel(), BorderLayout.WEST);
        add(buildSimulationPanel(), BorderLayout.CENTER);
        add(buildResultsPanel(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel buildSetupPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Simulation Setup"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        tfClients = addLabeledField(panel, gbc, row++, "Number of clients:", String.valueOf(manager.numberOfClients));
        tfQueues = addLabeledField(panel, gbc, row++, "Number of queues:", String.valueOf(manager.numberOfServers));
        tfTimeLimit = addLabeledField(panel, gbc, row++, "Time limit (s):", String.valueOf(manager.timeLimit));
        tfMinArrival = addLabeledField(panel, gbc, row++, "Min arrival time:", String.valueOf(manager.minArrivalTime));
        tfMaxArrival = addLabeledField(panel, gbc, row++, "Max arrival time:", String.valueOf(manager.maxArrivalTime));
        tfMinService = addLabeledField(panel, gbc, row++, "Min service time:", String.valueOf(manager.minProcessingTime));
        tfMaxService = addLabeledField(panel, gbc, row++, "Max service time:", String.valueOf(manager.maxProcessingTime));

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Strategy:"), gbc);
        gbc.gridx = 1;
        cbPolicy = new JComboBox<>(SelectionPolicy.values());
        cbPolicy.setSelectedItem(manager.selectionPolicy);
        panel.add(cbPolicy, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        btnStart = new JButton("Start Simulation");
        btnStart.addActionListener(e -> onStartClicked());
        panel.add(btnStart, gbc);

        return panel;
    }

    private JTextField addLabeledField(JPanel panel, GridBagConstraints gbc, int row, String label, String defaultVal) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JTextField tf = new JTextField(defaultVal, 8);
        panel.add(tf, gbc);
        return tf;
    }

    private JPanel buildSimulationPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Simulation"));

        lblCurrentTime = new JLabel("Time: 0");
        lblCurrentTime.setFont(lblCurrentTime.getFont().deriveFont(Font.BOLD, 14f));
        panel.add(lblCurrentTime, BorderLayout.NORTH);

        queuesPanel = new JPanel();
        queuesPanel.setLayout(new BoxLayout(queuesPanel, BoxLayout.Y_AXIS));
        JScrollPane queuesScroll = new JScrollPane(queuesPanel);
        queuesScroll.setBorder(new TitledBorder("Queues"));
        panel.add(queuesScroll, BorderLayout.CENTER);

        taWaitingClients = new JTextArea(3, 40);
        taWaitingClients.setEditable(false);
        taWaitingClients.setLineWrap(true);
        JScrollPane waitingScroll = new JScrollPane(taWaitingClients);
        waitingScroll.setBorder(new TitledBorder("Waiting Clients"));
        panel.add(waitingScroll, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildResultsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBorder(new TitledBorder("Results"));

        lblAvgWaiting = new JLabel("Avg waiting time: -");
        lblAvgService = new JLabel("Avg service time: -");
        lblPeakHour = new JLabel("Peak hour: -");

        panel.add(lblAvgWaiting);
        panel.add(lblAvgService);
        panel.add(lblPeakHour);

        return panel;
    }

    private void onStartClicked() {
        try {
            manager.numberOfClients = Integer.parseInt(tfClients.getText().trim());
            manager.numberOfServers = Integer.parseInt(tfQueues.getText().trim());
            manager.timeLimit = Integer.parseInt(tfTimeLimit.getText().trim());
            manager.minArrivalTime = Integer.parseInt(tfMinArrival.getText().trim());
            manager.maxArrivalTime = Integer.parseInt(tfMaxArrival.getText().trim());
            manager.minProcessingTime = Integer.parseInt(tfMinService.getText().trim());
            manager.maxProcessingTime = Integer.parseInt(tfMaxService.getText().trim());
            manager.selectionPolicy = (SelectionPolicy) cbPolicy.getSelectedItem();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid integer values.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (manager.minArrivalTime > manager.maxArrivalTime || manager.minProcessingTime > manager.maxProcessingTime) {
            JOptionPane.showMessageDialog(this, "Min values must be <= max values.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        btnStart.setEnabled(false);

        manager.reinitialize();
        Thread t = new Thread(manager);
        t.setDaemon(true);
        t.start();
    }

    public void update(int currentTime, List<Server> servers, List<Task> waitingTasks) {
        SwingUtilities.invokeLater(() -> {
            lblCurrentTime.setText("Time: " + currentTime);

            if (waitingTasks.isEmpty()) {
                taWaitingClients.setText("(none)");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Task t : waitingTasks) {
                    sb.append("(").append(t.getId()).append(", ")
                            .append(t.getArrivalTime()).append(", ")
                            .append(t.getServiceTime()).append(")  ");
                }
                taWaitingClients.setText(sb.toString());
            }

            queuesPanel.removeAll();
            for (int i = 0; i < servers.size(); i++) {
                Task[] tasks = servers.get(i).getTasks();
                StringBuilder sb = new StringBuilder();
                if (tasks.length == 0) {
                    sb.append("closed");
                } else {
                    for (Task t : tasks) {
                        sb.append("(").append(t.getId()).append(", ")
                                .append(t.getArrivalTime()).append(", ")
                                .append(t.getServiceTime()).append(")  ");
                    }
                }
                JLabel qLabel = new JLabel("Queue " + (i + 1) + ": " + sb);
                qLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
                queuesPanel.add(qLabel);
            }
            queuesPanel.revalidate();
            queuesPanel.repaint();
        });
    }

    public void showResults(double avgWaiting, double avgService, int peakHour) {
        SwingUtilities.invokeLater(() -> {
            lblAvgWaiting.setText("Avg waiting time: " + String.format("%.2f", avgWaiting));
            lblAvgService.setText("Avg service time: " + String.format("%.2f", avgService));
            lblPeakHour.setText("Peak hour: " + peakHour);
            btnStart.setEnabled(true);
        });
    }
}
