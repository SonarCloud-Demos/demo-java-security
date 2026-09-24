package demo.security.servlet;

import demo.security.util.SessionHeader;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class UserServletSessionAuthTest {

    @Test
    public void parseSessionAuth_withValidValue() {
        String encoded = Base64.getEncoder().encodeToString("alice:sess-123".getBytes(StandardCharsets.UTF_8));

        SessionHeader sessionHeader = UserServlet.parseSessionAuth(encoded);

        assertEquals("alice", sessionHeader.getUsername());
        assertEquals("sess-123", sessionHeader.getSessionId());
    }

    @Test
    public void parseSessionAuth_withNullValue() {
        assertNull(UserServlet.parseSessionAuth(null));
    }

    @Test
    public void parseSessionAuth_withoutDelimiter() {
        String encoded = Base64.getEncoder().encodeToString("alice".getBytes(StandardCharsets.UTF_8));

        assertNull(UserServlet.parseSessionAuth(encoded));
    }

    @Test
    public void parseSessionAuth_withSerializedBytes() {
        byte[] serializedMagic = {(byte) 0xAC, (byte) 0xED, 0x00, 0x05};
        String encoded = Base64.getEncoder().encodeToString(serializedMagic);

        assertNull(UserServlet.parseSessionAuth(encoded));
    }
}
