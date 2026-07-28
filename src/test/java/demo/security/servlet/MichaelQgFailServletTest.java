package demo.security.servlet;

import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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

  @Test
  public void escapeHtml_escapesSpecialCharacters() {
    assertEquals("", MichaelQgFailServlet.escapeHtml(null));
    assertEquals("a&amp;b", MichaelQgFailServlet.escapeHtml("a&b"));
    assertEquals("&lt;tag&gt;", MichaelQgFailServlet.escapeHtml("<tag>"));
    assertEquals("&quot;x&quot;", MichaelQgFailServlet.escapeHtml("\"x\""));
    assertEquals("&#39;x&#39;", MichaelQgFailServlet.escapeHtml("'x'"));
  }

  @Test
  public void lookupUser_withNullUser_returnsMessage() {
    assertEquals("no user specified", new MichaelQgFailServlet().lookupUser(null));
  }

    @Test
    public void queryUserId_whenUserExists_returnsId() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(connection.prepareStatement("SELECT userid FROM users WHERE username = ?"))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString(1)).thenReturn("42");

        assertEquals("42", new MichaelQgFailServlet().queryUserId(connection, "alice"));
        verify(statement).setString(1, "alice");
    }

    @Test
    public void queryUserId_whenUserMissing_returnsNotFound() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);
        when(connection.prepareStatement("SELECT userid FROM users WHERE username = ?"))
                .thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        assertEquals("not found", new MichaelQgFailServlet().queryUserId(connection, "bob"));
    }

    @Test
    public void lookupUser_whenQueryFails_returnsErrorMessage() throws Exception {
        MichaelQgFailServlet servlet =
                new MichaelQgFailServlet() {
                    @Override
                    Connection openConnection() throws SQLException {
                        throw new SQLException("connection refused");
                    }
                };

        assertTrue(servlet.lookupUser("alice").startsWith("error:"));
    }
}
