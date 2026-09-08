package presentation;

import businessLayer.ClientBLL;
import model.Client;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;

/**
 * GUI for Client management.
 * Allows users to add, edit, and delete clients while viewing them in a dynamic table.
 * @author Gabriella Danci
 */
public class ClientWindow extends JFrame {

    private final ClientBLL clientBLL = new ClientBLL();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField nameField     = new JTextField();
    private final JTextField addressField  = new JTextField();
    private final JTextField emailField    = new JTextField();
    private final JTextField idField       = new JTextField();

    /**
     * Initializes the client management interface.
     */
    public ClientWindow() {
        setTitle("Client Management");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Client Details"));
        idField.setEditable(false);
        form.add(new JLabel("Name:"));      form.add(nameField);
        form.add(new JLabel("Address:"));   form.add(addressField);
        form.add(new JLabel("Email:"));     form.add(emailField);

        JButton addBtn    = new JButton("Add");
        JButton editBtn   = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");

        addBtn.addActionListener(e    -> handleAdd());
        editBtn.addActionListener(e   -> handleEdit());
        deleteBtn.addActionListener(e -> handleDelete());

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(addBtn); buttons.add(editBtn); buttons.add(deleteBtn);

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(buttons, BorderLayout.SOUTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                addressField.setText(tableModel.getValueAt(row, 2).toString());
                emailField.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable();
        setVisible(true);
    }

    private void handleAdd() {
        try {
            clientBLL.insertClient(new Client(0, nameField.getText(), addressField.getText(), emailField.getText()));
            clearFields();
            refreshTable();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleEdit() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a client first."); return; }
        clientBLL.updateClient(new Client(id, nameField.getText(), addressField.getText(), emailField.getText()));
        clearFields();
        refreshTable();
    }

    private void handleDelete() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a client first."); return; }
        clientBLL.deleteClient(id);
        clearFields();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        List<Client> clients = clientBLL.findAllClients();
        if (clients == null || clients.isEmpty()) return;

        Field[] fields = Client.class.getDeclaredFields();
        for (Field f : fields) tableModel.addColumn(f.getName());

        for (Client c : clients) {
            Object[] row = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try { row[i] = fields[i].get(c); } catch (IllegalAccessException ex) { row[i] = ""; }
            }
            tableModel.addRow(row);
        }
    }

    private int getSelectedId() {
        String txt = idField.getText().trim();
        return txt.isEmpty() ? -1 : Integer.parseInt(txt);
    }

    private void clearFields() {
        idField.setText(""); nameField.setText(""); addressField.setText(""); emailField.setText("");
        table.clearSelection();
    }
}
