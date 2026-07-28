package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/michael-qg-fail")
public class MichaelQgFailServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("user");
        String message = request.getParameter("msg");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<html><body>");
        out.print("<p>Lookup result: " + escapeHtml(lookupUser(username)) + "</p>");
        out.print("<p>Your message: " + escapeHtml(message) + "</p>");
        out.print("</body></html>");
        out.close();
    }

    static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    String lookupUser(String user) {
        if (user == null) {
            return "no user specified";
        }
        try (Connection connection = openConnection()) {
            return queryUserId(connection, user);
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }

    String queryUserId(Connection connection, String user) throws SQLException {
        String sql = "SELECT userid FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
                return "not found";
            }
        }
    }

    Connection openConnection() throws SQLException {
        String url = System.getenv().getOrDefault("JDBC_URL", "jdbc:default");
        String username = System.getenv().getOrDefault("JDBC_USER", "");
        String password = System.getenv().getOrDefault("JDBC_PASSWORD", "");
        return DriverManager.getConnection(url, username, password);
    }
}
