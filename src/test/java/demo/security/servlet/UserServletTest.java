package demo.security.servlet;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertFalse;

public class UserServletTest {

    static class Probe implements Serializable {
        static boolean triggered = false;

        private void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            triggered = true;
        }
    }

    @Test
    public void doPost_rejectsNonAllowlistedClassInSessionAuthHeader() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(new Probe());
        }
        String encoded = org.apache.commons.codec.binary.Base64.encodeBase64String(baos.toByteArray());
        Probe.triggered = false;

        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> {
                    if ("getHeader".equals(method.getName()) && args != null && args.length == 1
                            && "Session-Auth".equals(args[0])) {
                        return encoded;
                    }
                    return null;
                });

        final boolean[] responseTouched = { false };
        HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] { HttpServletResponse.class },
                (proxy, method, args) -> {
                    responseTouched[0] = true;
                    return null;
                });

        new UserServlet().doPost(request, response);

        assertFalse(Probe.triggered);
        assertFalse(responseTouched[0]);
    }
}
