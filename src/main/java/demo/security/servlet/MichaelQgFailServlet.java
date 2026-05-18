package demo.security.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

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
        out.print("<p>Lookup result: " + lookupUser(username) + "</p>");
        out.print("<p>Your message: " + message + "</p>");
        out.print("</body></html>");
        out.close();
    }

    private String lookupUser(String user) {
        if (user == null) {
            return "no user specified";
        }
        try (Connection connection = DriverManager.getConnection(
                "jdbc:demo", "demo", "demo");
             Statement statement = connection.createStatement()) {
            String query = "SELECT userid FROM users WHERE username = '" + user + "'";
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                return resultSet.getString(1);
            }
            return "not found";
        } catch (Exception e) {
            return "error: " + e.getMessage();
        }
    }
}
