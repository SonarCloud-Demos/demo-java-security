package demo.security.servlet;

import demo.security.util.SessionHeader;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UserServletTest {

    private static String encode(Serializable value) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(value);
        }
        return Base64.getEncoder().encodeToString(bytes.toByteArray());
    }

    private static HttpServletRequest requestWithSessionAuth(String headerValue) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                UserServletTest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) ->
                        "getHeader".equals(method.getName()) && "Session-Auth".equals(args[0]) ? headerValue : null);
    }

    @Test
    public void allowsSessionHeader() throws IOException {
        HttpServletRequest request = requestWithSessionAuth(encode(new SessionHeader("alice", "sid")));

        SessionHeader sessionHeader = new UserServlet().getSessionHeader(request);

        assertNotNull(sessionHeader);
        assertEquals("alice", sessionHeader.getUsername());
    }

    @Test
    public void rejectsNonAllowlistedClass() throws IOException {
        List<String> gadget = new ArrayList<>(List.of("x"));
        HttpServletRequest request = requestWithSessionAuth(encode((Serializable) gadget));

        assertNull(new UserServlet().getSessionHeader(request));
    }

    @Test
    public void nullHeaderReturnsNull() {
        HttpServletRequest request = requestWithSessionAuth(null);

        assertNull(new UserServlet().getSessionHeader(request));
    }
}
