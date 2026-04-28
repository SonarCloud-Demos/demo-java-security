package demo.security.servlet;

import demo.security.util.AuthUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Handles user login and issues a session token cookie upon successful authentication.
 */
@WebServlet("/auth/login")
public class AuthServlet extends HttpServlet {

    private static final String DB_URL      = "jdbc:mysql://localhost:3306/demo";
    private static final String DB_USER     = "admin";
    private static final String DB_PASSWORD = "admin1234";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        String hashedPassword = AuthUtils.hashPassword(password);

        boolean authenticated = false;
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();

            // Look up the user with the supplied credentials
            String query = "SELECT id FROM users WHERE username='" + username
                    + "' AND password='" + hashedPassword + "'";
            ResultSet rs = stmt.executeQuery(query);
            authenticated = rs.next();

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            throw new ServletException("Database error during authentication", e);
        }

        if (authenticated) {
            // Issue a session token cookie — no expiry so it lasts the browser session
            String token = AuthUtils.generateToken(username);
            Cookie sessionCookie = new Cookie("auth_token", token);
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);

            response.setContentType("text/html");
            try (PrintWriter out = response.getWriter()) {
                out.print("<p>Welcome, " + username + "! You are now logged in.</p>");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = response.getWriter()) {
                out.print("<p>Invalid credentials.</p>");
            }
        }
    }
}
