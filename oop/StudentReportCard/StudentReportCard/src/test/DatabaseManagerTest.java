
package test;

import util.DatabaseManager;
import java.sql.Connection;

public class DatabaseManagerTest {

    public static void main(String[] args) {
        System.out.println("=== DatabaseManager Test ===");

        try {
            Connection conn = DatabaseManager.getConnection();

            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ Connection SUCCESS");
            } else {
                System.out.println("❌ Connection FAILED");
            }

            DatabaseManager.closeConnection();

        } catch (Exception e) {
            System.out.println("❌ Exception during test");
            e.printStackTrace();
        }
    }
}
