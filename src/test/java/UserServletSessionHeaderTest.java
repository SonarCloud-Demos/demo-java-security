import demo.security.servlet.UserServlet;
import demo.security.util.SessionHeader;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Base64;
import java.util.HashMap;

/**
 * Plain, dependency-free check for UserServlet#getSessionHeader hardening.
 * Run directly (it has a main method) rather than through a test framework,
 * since only standard library and project classes may be imported here.
 */
public class UserServletSessionHeaderTest {

    public static void main(String[] args) throws Exception {
        UserServletSessionHeaderTest test = new UserServletSessionHeaderTest();
        test.getSessionHeader_rejectsNonAllowlistedClass();
        test.getSessionHeader_acceptsSessionHeader();
        System.out.println("All tests passed.");
    }

    private HttpServletRequest requestWithSessionAuthHeader(String headerValue) {
        InvocationHandler handler = (proxy, method, methodArgs) -> {
            if ("getHeader".equals(method.getName()) && methodArgs != null && methodArgs.length == 1
                    && "Session-Auth".equals(methodArgs[0])) {
                return headerValue;
            }
            Class<?> returnType = method.getReturnType();
            if (returnType == boolean.class) return false;
            if (returnType == int.class) return 0;
            if (returnType == long.class) return 0L;
            return null;
        };
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[]{HttpServletRequest.class},
                handler);
    }

    private String serializeToBase64(Object object) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
        }
        return Base64.getEncoder().encodeToString(bos.toByteArray());
    }

    private SessionHeader invokeGetSessionHeader(HttpServletRequest request) throws Exception {
        Method m = UserServlet.class.getDeclaredMethod("getSessionHeader", HttpServletRequest.class);
        m.setAccessible(true);
        return (SessionHeader) m.invoke(new UserServlet(), request);
    }

    public void getSessionHeader_rejectsNonAllowlistedClass() throws Exception {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("key", "value");
        String encoded = serializeToBase64(payload);

        HttpServletRequest request = requestWithSessionAuthHeader(encoded);

        SessionHeader result = invokeGetSessionHeader(request);
        if (result != null) {
            throw new AssertionError("Expected null for non-allowlisted class, got: " + result);
        }
    }

    public void getSessionHeader_acceptsSessionHeader() throws Exception {
        String encoded = serializeToBase64(new SessionHeader("alice", "sid"));

        HttpServletRequest request = requestWithSessionAuthHeader(encoded);

        SessionHeader result = invokeGetSessionHeader(request);
        if (result == null) {
            throw new AssertionError("Expected a SessionHeader, got null");
        }
        if (!"alice".equals(result.getUsername())) {
            throw new AssertionError("Expected username 'alice', got: " + result.getUsername());
        }
    }
}
