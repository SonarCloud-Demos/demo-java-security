package demo.security;

import demo.security.util.WebUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.UncheckedIOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class WebUtilsTest {

    @Test
    void getSessionId_whenNoSession_doesNothing() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getSession(false)).thenReturn(null);

        assertDoesNotThrow(() -> WebUtils.getSessionId(request));
    }

    @Test
    void getSessionId_whenSessionPresent_attemptsSocketAndMayFail() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getId()).thenReturn("validSessionId");

        assertThrows(UncheckedIOException.class, () -> WebUtils.getSessionId(request));
    }

    @Test
    void getSessionId_whenGetSessionThrows_propagates() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(request.getSession(false)).thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class, () -> WebUtils.getSessionId(request));
    }
}
