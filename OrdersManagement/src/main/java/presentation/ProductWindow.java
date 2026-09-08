package presentation;

import businessLayer.ProductBLL;
import model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;

/**
 * GUI window for adding editing and viewing products.
 * @author Gabriella Danci
 */
public class ProductWindow extends JFrame {

    private final ProductBLL productBLL = new ProductBLL();
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextField idField       = new JTextField();
    private final JTextField nameField     = new JTextField();
    private final JTextField priceField    = new JTextField();
    private final JTextField quantityField = new JTextField();

    /**
     * Initializes the product managing interface.
     */
    public ProductWindow() {
        setTitle("Product Management");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("Product Details"));
        idField.setEditable(false);
        form.add(new JLabel("ID (auto):")); form.add(idField);
        form.add(new JLabel("Name:"));      form.add(nameField);
        form.add(new JLabel("Price:"));     form.add(priceField);
        form.add(new JLabel("Quantity:"));  form.add(quantityField);

        // Buttons
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

        // Table row selection fills the form
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                idField.setText(tableModel.getValueAt(row, 0).toString());
                nameField.setText(tableModel.getValueAt(row, 1).toString());
                priceField.setText(tableModel.getValueAt(row, 2).toString());
                quantityField.setText(tableModel.getValueAt(row, 3).toString());
            }
        });

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable();
        setVisible(true);
    }

    private void handleAdd() {
        try {
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            productBLL.insertProduct(new Product(0, nameField.getText(), price, quantity));
            clearFields();
            refreshTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and quantity must be numeric.");
        }
    }

    private void handleEdit() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a product first."); return; }
        try {
            double price = Double.parseDouble(priceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());
            productBLL.updateProduct(new Product(id, nameField.getText(), price, quantity));
            clearFields();
            refreshTable();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Price and quantity must be numeric.");
        }
    }

    private void handleDelete() {
        int id = getSelectedId();
        if (id < 0) { JOptionPane.showMessageDialog(this, "Select a product first."); return; }
        productBLL.deleteProduct(id);
        clearFields();
        refreshTable();
    }

    /** Uses reflection to build column headers and populate rows (Requirement 4d). */
    private void refreshTable() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        List<Product> products = productBLL.findAllProducts();
        if (products == null || products.isEmpty()) return;

        Field[] fields = Product.class.getDeclaredFields();
        for (Field f : fields) tableModel.addColumn(f.getName());

        for (Product p : products) {
            Object[] row = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try { row[i] = fields[i].get(p); } catch (IllegalAccessException ex) { row[i] = ""; }
            }
            tableModel.addRow(row);
        }
    }

    private int getSelectedId() {
        String txt = idField.getText().trim();
        return txt.isEmpty() ? -1 : Integer.parseInt(txt);
    }

    private void clearFields() {
        idField.setText(""); nameField.setText(""); priceField.setText(""); quantityField.setText("");
        table.clearSelection();
    }
}
