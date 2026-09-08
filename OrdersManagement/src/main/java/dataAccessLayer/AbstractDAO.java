package dataAccessLayer;

import connection.ConnectionFactory;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.logging.*;
/**
 * @author Gabriella Danci
 * Source: http://www.java-blog.com/mapping-javaobjects-database-reflection-generics
 * Data Access Object that uses Java Reflection to perform CRUD operations on any
 * model class.
 */

public class AbstractDAO<T> {
    /** Logger for recording database errors. */
    protected static final Logger LOGGER = Logger.getLogger(AbstractDAO.class.getName());
    private final Class<T> type;

    /**
     * Initializes the DAO by determining the class type.
     */
    @SuppressWarnings("unchecked")
    public AbstractDAO() {
        this.type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private String createSelectQuery(String field) {
        return "SELECT * FROM `" + type.getSimpleName() + "` WHERE " + field + " = ?";
    }

    private String createSelectAllQuery() {
        return "SELECT * FROM `" + type.getSimpleName() + "`";
    }

    /**
     * Retrieves all records from the corresponding table.
     * @return a list of all objects
     */
    public List<T> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectAllQuery();
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();
            return createObjects(resultSet);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findAll " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    /**
     * Finds a record by its unique identifier.
     * @param id the record ID
     * @return the found object
     */
    public T findById(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String query = createSelectQuery("id");
        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            return createObjects(resultSet).get(0);
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:findById " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return null;
    }

    private List<T> createObjects(ResultSet resultSet) {
        List<T> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                T instance = type.getDeclaredConstructor().newInstance();
                for (Field field : type.getDeclaredFields()) {
                    Object value = resultSet.getObject(field.getName());
                    PropertyDescriptor propertyDescriptor = new PropertyDescriptor(field.getName(), type);
                    Method method = propertyDescriptor.getWriteMethod();
                    method.invoke(instance, value);
                }
                list.add(instance);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error creating objects via reflection: " + e.getMessage());
        }
        return list;
    }

    /**
     * Inserts a new object into the database.
     * @param t the object to insert
     * @return the ID of the new record
     */
    public int insert(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        StringBuilder query = new StringBuilder("INSERT INTO `").append(type.getSimpleName()).append("` (");
        StringBuilder values = new StringBuilder("VALUES (");

        try {
            Field[] fields = type.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if (!fields[i].getName().equals("id")) { // Skip auto-increment ID
                    query.append(fields[i].getName()).append(i < fields.length - 1 ? ", " : "");
                    values.append("?").append(i < fields.length - 1 ? ", " : "");
                }
            }
            query.append(") ").append(values).append(")");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);

            int paramIndex = 1;
            for (Field field : fields) {
                if (!field.getName().equals("id")) {
                    statement.setObject(paramIndex++, field.get(t));
                }
            }

            statement.executeUpdate();
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) return resultSet.getInt(1); // Return the new ID
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:insert " + e.getMessage());
        } finally {
            ConnectionFactory.close(resultSet);
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
        return -1;
    }

    /**
     * Updates an existing record.
     * @param t the object with updated data
     */
    public void update(T t) {
        Connection connection = null;
        PreparedStatement statement = null;
        StringBuilder query = new StringBuilder("UPDATE `").append(type.getSimpleName()).append("` SET ");

        try {
            Field[] fields = type.getDeclaredFields();
            Object idValue = null;

            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if (!fields[i].getName().equals("id")) {
                    query.append(fields[i].getName()).append(" = ?").append(i < fields.length - 1 ? ", " : "");
                } else {
                    idValue = fields[i].get(t);
                }
            }
            query.append(" WHERE id = ?");

            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query.toString());

            int paramIndex = 1;
            for (Field field : fields) {
                if (!field.getName().equals("id")) {
                    statement.setObject(paramIndex++, field.get(t));
                }
            }
            statement.setObject(paramIndex, idValue);
            statement.executeUpdate();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:update " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }

    /**
     * Deletes a record by ID.
     * @param id the ID to delete
     */
    public void delete(int id) {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = "DELETE FROM `" + type.getSimpleName() + "` WHERE id = ?";

        try {
            connection = ConnectionFactory.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, type.getName() + "DAO:delete " + e.getMessage());
        } finally {
            ConnectionFactory.close(statement);
            ConnectionFactory.close(connection);
        }
    }
}
