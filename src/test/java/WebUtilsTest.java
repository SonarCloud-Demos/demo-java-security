import demo.security.util.WebUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class WebUtilsTest {

    @Test
    public void getSessionId_withValidRequest() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestedSessionId()).thenReturn("validSessionId");

        assertThrows(RuntimeException.class, () -> WebUtils.getSessionId(request));
    }

    @Test
    public void getSessionId_withNullSessionId() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestedSessionId()).thenReturn(null);

        WebUtils.getSessionId(request);
        Mockito.verify(request).getRequestedSessionId();
    }

    @Test
    public void getSessionId_withIOException() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getRequestedSessionId()).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> WebUtils.getSessionId(request));
    }
}