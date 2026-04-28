package demo.security.servlet;

import demo.security.util.HtmlEscapes;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/helloWorld")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws ServletException, IOException {
        String nameParam = request.getParameter("name");
        String name = nameParam == null ? "" : nameParam.trim();
        response.setContentType("text/html");
        writeResponse(response, name);
    }

    protected void writeResponse(HttpServletResponse response, String name) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            out.print("<h2>Hello " + HtmlEscapes.escape(name) + "</h2>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
