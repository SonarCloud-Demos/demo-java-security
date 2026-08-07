package demo.security.util;

import javax.servlet.http.HttpServletRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBUtils {

    Connection connection;
    public DBUtils() throws SQLException {
        String jdbcUrl = System.getenv("JDBC_URL");
        String jdbcUser = System.getenv("JDBC_USER");
        String jdbcPass = System.getenv("JDBC_PASSWORD");
        connection = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPass);
    }

    public List<String> findUsers(String user) throws Exception {
        String query = "SELECT userid FROM users WHERE username = '" + user  + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<String> users = new ArrayList<String>();
        while (resultSet.next()){
            users.add(resultSet.getString(0));
        }
        return users;
    }

    public List<String> findItem(String itemId) throws Exception {
        String query = "SELECT item_id FROM items WHERE item_id = '" + itemId  + "'";
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        List<String> items = new ArrayList<String>();
        while (resultSet.next()){
            items.add(resultSet.getString(0));
        }
        return items;
    }
}
