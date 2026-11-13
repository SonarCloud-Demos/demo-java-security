package demo.security.servlet;

import demo.security.util.SessionHeader;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UserServletFilterTest {

    @Test
    public void allowsSessionHeader() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(new SessionHeader("alice", "s1"));
        }

        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            in.setObjectInputFilter(UserServlet.SESSION_HEADER_FILTER);
            SessionHeader sessionHeader = (SessionHeader) in.readObject();
            assertEquals("alice", sessionHeader.getUsername());
        }
    }

    @Test
    public void rejectsNonAllowlistedClass() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(new ArrayList<String>());
        }

        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            in.setObjectInputFilter(UserServlet.SESSION_HEADER_FILTER);
            assertThrows(InvalidClassException.class, in::readObject);
        }
    }
}
