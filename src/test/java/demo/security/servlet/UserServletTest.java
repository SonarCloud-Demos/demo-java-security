package demo.security.servlet;

import demo.security.util.SessionHeader;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServletTest {

    private static class Probe implements Serializable {
        static volatile boolean read = false;

        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            read = true;
            in.defaultReadObject();
        }
    }

    private static String encode(Object object) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(byteArrayOutputStream)) {
            out.writeObject(object);
        }
        return Base64.encodeBase64String(byteArrayOutputStream.toByteArray());
    }

    @Test
    public void getSessionHeader_rejectsNonAllowlistedClass() throws IOException {
        Probe.read = false;
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Session-Auth")).thenReturn(encode(new Probe()));

        SessionHeader result = new UserServlet().getSessionHeader(request);

        assertNull(result);
        assertFalse(Probe.read);
    }

    @Test
    public void getSessionHeader_acceptsSessionHeader() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Session-Auth")).thenReturn(encode(new SessionHeader("alice", "sid")));

        SessionHeader result = new UserServlet().getSessionHeader(request);

        assertNotNull(result);
        assertEquals("alice", result.getUsername());
    }
}
