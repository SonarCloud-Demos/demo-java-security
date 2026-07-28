package demo.security.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import demo.security.util.DBUtils;
import demo.security.util.HtmlEscapes;
import demo.security.util.SessionHeader;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    private static final ObjectMapper SESSION_HEADER_JSON = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("username");
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            try (PrintWriter out = response.getWriter()) {
                users.forEach(result -> out.print("<h2>User " + HtmlEscapes.escape(result) + "</h2>"));
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private SessionHeader getSessionHeader(HttpServletRequest request) {
        String sessionAuth = request.getHeader("Session-Auth");
        if (sessionAuth == null) {
            return null;
        }
        try {
            byte[] decoded = Base64.decodeBase64(sessionAuth);
            JsonNode root = SESSION_HEADER_JSON.readTree(decoded);
            if (root == null || !root.hasNonNull("username")) {
                return null;
            }
            SessionHeader header = new SessionHeader();
            header.setUsername(root.get("username").asText());
            if (root.hasNonNull("sessionId")) {
                header.setSessionId(root.get("sessionId").asText());
            }
            return header;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionHeader sessionHeader = getSessionHeader(request);
        if (sessionHeader == null) {
            return;
        }
        String user = sessionHeader.getUsername();
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            try (PrintWriter out = response.getWriter()) {
                users.forEach(result -> out.print("<h2>User " + HtmlEscapes.escape(result) + "</h2>"));
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
