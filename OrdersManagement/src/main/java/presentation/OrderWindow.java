package presentation;

import businessLayer.ClientBLL;
import businessLayer.OrderBLL;
import businessLayer.ProductBLL;
import model.Client;
import model.Order;
import model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;

/**
 * GUI window for creating and viewing product orders.
 * @author Gabriella Danci
 */
public class OrderWindow extends JFrame {

    private final OrderBLL   orderBLL   = new OrderBLL();
    private final ClientBLL  clientBLL  = new ClientBLL();
    private final ProductBLL productBLL = new ProductBLL();

    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);
    private final JComboBox<String> clientCombo  = new JComboBox<>();
    private final JComboBox<String> productCombo = new JComboBox<>();
    private final JTextField quantityField = new JTextField();

    private List<Client>  clients;
    private List<Product> products;

    /**
     * Initializes the order creation interface.
     */
    public OrderWindow() {
        setTitle("Order Management");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(BorderFactory.createTitledBorder("New Order"));
        form.add(new JLabel("Client:"));   form.add(clientCombo);
        form.add(new JLabel("Product:"));  form.add(productCombo);
        form.add(new JLabel("Quantity:")); form.add(quantityField);

        JButton placeBtn = new JButton("Place Order");
        placeBtn.addActionListener(e -> handlePlaceOrder());

        JPanel top = new JPanel(new BorderLayout());
        top.add(form, BorderLayout.CENTER);
        top.add(placeBtn, BorderLayout.SOUTH);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshAll();
        setVisible(true);
    }

    private void handlePlaceOrder() {
        int clientIdx  = clientCombo.getSelectedIndex();
        int productIdx = productCombo.getSelectedIndex();

        if (clientIdx < 0 || productIdx < 0) {
            JOptionPane.showMessageDialog(this, "Select a client and a product.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a number.");
            return;
        }

        int clientId  = clients.get(clientIdx).getId();
        int productId = products.get(productIdx).getId();

        try {
            orderBLL.createOrder(clientId, productId, quantity);
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
            quantityField.setText("");
            refreshAll();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Order Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Reloads combos and order table. */
    private void refreshAll() {
        clients = clientBLL.findAllClients();
        products = productBLL.findAllProducts();
        clientCombo.removeAllItems();
        productCombo.removeAllItems();
        if (clients != null)  clients.forEach(c  -> clientCombo.addItem(c.getId()  + " - " + c.getName()));
        if (products != null) products.forEach(p -> productCombo.addItem(p.getId() + " - " + p.getName() + " (stock: " + p.getQuantity() + ")"));

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        List<Order> orders = orderBLL.findAllOrders();
        if (orders == null || orders.isEmpty()) return;

        Field[] fields = Order.class.getDeclaredFields();
        for (Field f : fields) tableModel.addColumn(f.getName());

        for (Order o : orders) {
            Object[] row = new Object[fields.length];
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try { row[i] = fields[i].get(o); } catch (IllegalAccessException ex) { row[i] = ""; }
            }
            tableModel.addRow(row);
        }
    }
}
