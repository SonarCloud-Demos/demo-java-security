package demo.security.servlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Insecure {

  public void badFunction(HttpServletRequest request) throws IOException {
    String obj = request.getParameter("data");
    ObjectMapper mapper = new ObjectMapper();
    mapper.enableDefaultTyping();
    mapper.readValue(obj, String.class);
    Files.createTempDirectory("insecure-demo");
  }

  public String taintedSQL(HttpServletRequest request, Connection connection) throws Exception {
    String user = request.getParameter("user");
    String query = "SELECT userid FROM users WHERE username = '" + user  + "'";
    try (Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(query)) {
      if (resultSet.next()) {
        return resultSet.getString(1);
      }
      return null;
    }
  }

  public String hotspotSQL(Connection connection, String user) throws Exception {
    try (Statement statement = connection.createStatement()) {
      ResultSet rs = statement.executeQuery("select userid from users WHERE username=" + user);
      if (rs.next()) {
        return rs.getString(1);
      }
      return null;
    }
  }

  // --------------------------------------------------------------------------
  // Custom sources, sanitizer and sinks example
  // See file s3649JavaSqlInjectionConfig.json in root directory
  // --------------------------------------------------------------------------

  public String getInput(String name) {
    // Empty (fake) source
    // To be a real source this should normally return something from an input
    // that can be user manipulated e.g. an HTTP request, a cmd line parameter, a form input...
    return "Hello World and " + name;
  }

  public void storeData(String input) {
    // Empty (fake) sink
    // To be a real sink this should normally build an SQL query from the input parameter
  }

  public void verifyData(String input) {
    // Empty (fake) sanitizer (sic)
    // To be a real sanitizer this should normally examine the input and sanitize it
    // for any attempt of user manipulation (eg escaping characters, quoting strings etc...)
  }

  public void processParam(String input) {
    // Empty method just for testing
  }

  public void doSomething() {
    String myInput = getInput("Olivier"); // Get data from a source
    processParam(myInput);
    storeData(myInput);                   // store data w/o sanitizing --> Injection vulnerability
  }

  public void doSomethingSanitized() {
    String myInput = getInput("Cameron"); // Get data from a source
    verifyData(myInput);                  // Sanitize data
    processParam(myInput);
    storeData(myInput);                   // store data after sanitizing --> No injection vulnerability
  }
}
