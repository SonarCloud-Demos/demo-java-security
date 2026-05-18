package demo.security.servlet;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MichaelQgFailServletTest {

    @Test
    public void doGet_withoutUser_reportsMissingUser() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(request.getParameter("user")).thenReturn(null);
        when(request.getParameter("msg")).thenReturn("hello");
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        new MichaelQgFailServlet().doGet(request, response);

        assertTrue(body.toString().contains("no user specified"));
        assertTrue(body.toString().contains("hello"));
    }

    @Test
    public void doGet_withUser_handlesDatabaseError() throws Exception {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter body = new StringWriter();
        when(request.getParameter("user")).thenReturn("alice");
        when(request.getParameter("msg")).thenReturn(null);
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        new MichaelQgFailServlet().doGet(request, response);

        assertTrue(body.toString().contains("error:"));
    }
}
