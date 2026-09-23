import demo.security.servlet.UserServlet;
import demo.security.util.SessionHeader;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class UserServletDeserializationTest {

    private static String serializeToBase64(Object object) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
        }
        return Base64.encodeBase64String(bos.toByteArray());
    }

    @Test
    public void deserializeSessionHeader_withAllowedClass_returnsSessionHeader() throws IOException {
        String encoded = serializeToBase64(new SessionHeader("alice", "s1"));

        SessionHeader result = UserServlet.deserializeSessionHeader(encoded);

        assertNotNull(result);
        assertEquals("alice", result.getUsername());
    }

    @Test
    public void deserializeSessionHeader_withDisallowedClass_returnsNull() throws IOException {
        String encoded = serializeToBase64(new HashMap<String, String>());

        SessionHeader result = UserServlet.deserializeSessionHeader(encoded);

        assertNull(result);
    }
}
