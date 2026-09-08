package businessLayer;

import dataAccessLayer.ClientDAO;
import model.Client;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Business Logic Layer for Client operations.
 * Handles validation and interacts with the ClientDAO.
 * @author Gabriella Danci
 */
public class ClientBLL {
    private ClientDAO clientDAO;

    /**
     * Constructs a new ClientBLL and initializes its DAO.
     */
    public ClientBLL() {
        clientDAO = new ClientDAO();
    }

    /**
     * Retrieves all clients from the database.
     * @return A list of all clients.
     */
    public List<Client> findAllClients() {
        return clientDAO.findAll();
    }

    /**
     * Validates and inserts a new client into the database.
     * @param client The client to insert.
     * @return The ID of the newly inserted client.
     */
    public int insertClient(Client client) {
        validate(client);
        return clientDAO.insert(client);
    }

    /**
     * Validates and updates an existing client.
     * @param client The client with updated information.
     */
    public void updateClient(Client client) {
        validate(client);
        clientDAO.update(client);
    }

    /**
     * Deletes a client from the database.
     * @param id The ID of the client to delete.
     */
    public void deleteClient(int id) {
        clientDAO.delete(id);
    }

    private void validate(Client client) {
        if (client.getAddress() == null || client.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty!");
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        if (client.getEmail() == null || !pat.matcher(client.getEmail()).matches()) {
            throw new IllegalArgumentException("Invalid email format!");
        }

        if (client.getName() == null || client.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty!");
        }
    }
}