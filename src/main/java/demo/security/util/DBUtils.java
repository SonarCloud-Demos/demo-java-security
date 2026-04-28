package demo.security.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    private final Connection connection;

    public DBUtils() throws SQLException {
        String url = System.getenv().getOrDefault("DEMO_JDBC_URL", "jdbc:noop://localhost/demo");
        String user = System.getenv().getOrDefault("DEMO_JDBC_USER", "demo");
        String password = System.getenv().getOrDefault("DEMO_JDBC_PASSWORD", "");
        connection = DriverManager.getConnection(url, user, password);
    }

    public List<String> findUsers(String user) throws SQLException {
        String query = "SELECT userid FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> users = new ArrayList<>();
                while (resultSet.next()) {
                    users.add(resultSet.getString(1));
                }
                return users;
            }
        }
    }

    public List<String> findItem(String itemId) throws SQLException {
        String query = "SELECT item_id FROM items WHERE item_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, itemId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<String> items = new ArrayList<>();
                while (resultSet.next()) {
                    items.add(resultSet.getString(1));
                }
                return items;
            }
        }
    }
}
