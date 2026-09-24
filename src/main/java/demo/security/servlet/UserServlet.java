package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.SessionHeader;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/users")
public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String user = request.getParameter("username");
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            users.forEach((result) -> {
                        out.print("<h2>User "+result+ "</h2>");
            });
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private SessionHeader getSessionHeader(HttpServletRequest request) {
        return parseSessionAuth(request.getHeader("Session-Auth"));
    }

    static SessionHeader parseSessionAuth(String sessionAuth) {
        if (sessionAuth == null || sessionAuth.trim().isEmpty()) {
            return null;
        }
        try {
            byte[] decoded = Base64.decodeBase64(sessionAuth);
            String decodedText = new String(decoded, StandardCharsets.UTF_8);
            int separatorIndex = decodedText.indexOf(':');
            if (separatorIndex <= 0 || separatorIndex == decodedText.length() - 1) {
                return null;
            }
            String username = decodedText.substring(0, separatorIndex);
            String sessionId = decodedText.substring(separatorIndex + 1);
            return new SessionHeader(username, sessionId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        SessionHeader sessionHeader = getSessionHeader(request);
        if (sessionHeader == null) return;
        String user = sessionHeader.getUsername();
        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            users.forEach((result) -> {
                out.print("<h2>User "+result+ "</h2>");
            });
            out.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
