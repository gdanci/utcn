package businessLayer;

import dataAccessLayer.*;
import model.*;
import java.util.List;

/**
 * @author Gabriella Danci
 * Business Logic for processing orders.
 */
public class OrderBLL {
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private ClientDAO clientDAO;

    public OrderBLL() {
        orderDAO = new OrderDAO();
        productDAO = new ProductDAO();
        clientDAO = new ClientDAO();
    }

    /**
     * Creates an order and decrements product stock.
     * @param clientId customer ID
     * @param productId product ID
     * @param quantity amount requested
     * @throws Exception if stock is insufficient
     */
    public void createOrder(int clientId, int productId, int quantity) throws Exception {
        Product product = productDAO.findById(productId);
        Client client = clientDAO.findById(clientId);

        if (product.getQuantity() < quantity) {
            throw new Exception("Under-stock message: Not enough products in warehouse!");
        }

        Order order = new Order(clientId, productId, quantity);
        int orderId = orderDAO.insert(order);

        product.setQuantity(product.getQuantity() - quantity);
        productDAO.update(product);

    }

    public List<Order> findAllOrders() {
        return orderDAO.findAll();
    }
}