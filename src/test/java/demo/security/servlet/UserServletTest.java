package demo.security.servlet;

import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Base64;

import static org.junit.Assert.assertFalse;

public class UserServletTest {

    static class Probe implements Serializable {
        static boolean invoked = false;

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            invoked = true;
            in.defaultReadObject();
        }
    }

    @Test
    public void doPost_rejectsNonAllowlistedGadget() throws Exception {
        Probe.invoked = false;

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(new Probe());
        }
        String sessionAuth = Base64.getEncoder().encodeToString(bos.toByteArray());

        InvocationHandler requestHandler = (proxy, method, args) ->
                "getHeader".equals(method.getName()) && args != null && args.length == 1
                        && "Session-Auth".equals(args[0]) ? sessionAuth : null;
        HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[]{HttpServletRequest.class}, requestHandler);

        InvocationHandler responseHandler = (proxy, method, args) -> null;
        HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class<?>[]{HttpServletResponse.class}, responseHandler);

        new UserServlet().doPost(request, response);

        assertFalse(Probe.invoked);
    }
}
