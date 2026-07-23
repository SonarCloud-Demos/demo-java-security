package demo.security.servlet;

import demo.security.util.DBUtils;
import demo.security.util.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Advanced SAST demo: taint crosses first-party helpers and third-party libraries
 * (Apache Commons IO / Codec) before reaching path, SQL, and XSS sinks.
 */
@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Source (1st) → Utils.loadDocument (1st) → FilenameUtils/FileUtils (3rd) → XSS (1st)
        String documentId = request.getParameter("id");
        String content = Utils.loadDocument(documentId);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.print("<pre>" + content + "</pre>");
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Source (1st) → Utils.decodeUserToken → commons-codec Base64 (3rd) → SQL (1st)
        String encodedUser = request.getParameter("token");
        String user = Utils.decodeUserToken(encodedUser);

        try {
            DBUtils db = new DBUtils();
            List<String> users = db.findUsers(user);
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            for (String result : users) {
                out.print("<h2>User " + result + "</h2>");
            }
            out.close();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Source (1st) → Utils.readRequestBody → IOUtils (3rd) → SQL (1st)
        String itemId = Utils.readRequestBody(request.getInputStream());

        try {
            DBUtils db = new DBUtils();
            List<String> items = db.findItem(itemId);
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            for (String item : items) {
                out.println(item);
            }
            out.close();
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
