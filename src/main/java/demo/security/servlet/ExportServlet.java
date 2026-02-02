package demo.security.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Generates export reports. Accepts report type from request and invokes
 * the report generator script.
 */
@WebServlet("/export")
public class ExportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String reportType = request.getParameter("type");
        String format = request.getParameter("format");
        if (reportType == null || reportType.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Report type required");
            return;
        }
        try {
            String output;
            // Legacy format uses shell script with type as argument
            if ("legacy".equals(format)) {
                output = runLegacyReportScript(reportType);
            } else {
                output = runReportGenerator(reportType);
            }
            response.setContentType("text/plain");
            response.getWriter().write(output);
        } catch (Exception e) {
            throw new ServletException("Export failed", e);
        }
    }

    /** Legacy report generation - invokes script with type passed to shell */
    private String runLegacyReportScript(String reportType) throws IOException {
        String cmd = "/opt/app/scripts/generate-report.sh " + reportType;
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }

    private String runReportGenerator(String reportType) throws IOException {
        String scriptPath = "/opt/app/scripts/generate-report.sh";
        Process process = Runtime.getRuntime().exec(new String[]{scriptPath, reportType});
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }
        return output.toString();
    }
}
