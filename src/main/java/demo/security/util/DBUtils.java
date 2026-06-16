package demo.security.util;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    Connection connection;
    public DBUtils() throws SQLException {
        connection = DriverManager.getConnection(
                System.getenv("JDBC_URL"), System.getenv("JDBC_USER"), System.getenv("JDBC_PASS"));
    }

    public List<String> findUsers(String user) throws Exception {
        String query = "SELECT userid FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user);
            ResultSet resultSet = statement.executeQuery();
            List<String> users = new ArrayList<String>();
            while (resultSet.next()){
                users.add(resultSet.getString(1));
            }
            return users;
        }
    }

    public List<String> findItem(String itemId) throws Exception {
        String query = "SELECT item_id FROM items WHERE item_id = '" + itemId  + "'";
        List<String> items = new ArrayList<String>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()){
                items.add(resultSet.getString(1));
            }
        }
        return items;
    }
}
