package com.follett.mywebapp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CodeBuilderServiceImpl extends RemoteServiceServlet implements CodeBuilderService {

  private static final long serialVersionUID = 1L;

public String greetServer(String input) throws IllegalArgumentException  {
    // Verify that the input is valid.

    String serverInfo = getServletContext().getServerInfo();
    String userAgent = getThreadLocalRequest().getHeader("User-Agent");

    // Escape data from the client to avoid cross-site script vulnerabilities.
    input = escapeHtml(input);
    userAgent = escapeHtml(userAgent);

    return "Hello, " + input + "!<br><br>I am running " + serverInfo
        + ".<br><br>It looks like you are using:<br>" + userAgent;
  }

  /**
   * Escape an html string. Escaping data received from the client helps to
   * prevent cross-site script vulnerabilities.
   *
   * @param html the html string to escape
   * @return the escaped string
   */
  private String escapeHtml(String html) {
    if (html == null) {
      return null;
    }
    return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(
        ">", "&gt;");
  }

  /**
   * Implement the connection to the server and gather the necessary data
   * */
  @Override
  public ArrayList<String> getCodePieces() {
	  ArrayList<String> returnable = new ArrayList<String>();
		  try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  // TODO get tree items and build them into the tree
		  String url = "jdbc:sqlserver://127.0.0.1:1433;" +
		  "databaseName=Rain;user=sa;password=stuffy;";
		  Connection conn;
		try {
			conn = DriverManager.getConnection(url);
		  Statement stmt = conn.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT * FROM dbo.TreeItems ORDER BY parentTagID");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	  return returnable;
  }
}
