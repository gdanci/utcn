package businessLayer;

import dataAccessLayer.ProductDAO;
import model.Product;
import java.util.List;

/**
 * Business Logic Layer for Product operations.
 * @author Gabriella Danci
 */
public class ProductBLL {
    private ProductDAO productDAO;

    /**
     * Constructs a new ProductBLL and initializes its DAO.
     */
    public ProductBLL() {
        productDAO = new ProductDAO();
    }

    /**
     * Retrieves all products from the warehouse.
     * @return List of all products.
     */
    public List<Product> findAllProducts() {
        return productDAO.findAll();
    }

    /**
     * Validates and inserts a new product into the database.
     * @param product The product to insert.
     * @return The ID of the newly inserted product.
     */
    public int insertProduct(Product product) {
        return productDAO.insert(product);
    }

    /**
     * Validates and updates an existing product.
     * @param product The product with updated information.
     */
    public void updateProduct(Product product) {
        productDAO.update(product);
    }

    /**
     * Deletes a product from the database.
     * @param id The ID of the product to delete.
     */
    public void deleteProduct(int id) {
        productDAO.delete(id);
    }
}