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
public class CodeBuilderServiceImpl extends RemoteServiceServlet implements CodeBuilderService {

  private static final long serialVersionUID = 1L;
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
		  ResultSet rs = stmt.executeQuery("SELECT * FROM dbo.Code ORDER BY tagID");
	  } catch (SQLException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  }

	  return returnable;
  }
@Override
public Boolean saveTest(int testNumber, String testSteps) {
	  Boolean exception = Boolean.FALSE;
	  try {
		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		  String url = "jdbc:sqlserver://127.0.0.1:1433;" +
		  "databaseName=Rain;user=sa;password=stuffy;";
		  Connection conn = DriverManager.getConnection(url);
		  Statement stmt = conn.createStatement();
		  String sql = "";
		  sql = "UPDATE dbo.Tests SET " +
		  "Steps = '" + testSteps + "' " +
		  "WHERE TestNumber = '" + testNumber + "'";
		  int success = stmt.executeUpdate(sql);
		  if(success == 0) {
			  sql = "INSERT INTO dbo.Tests " +
			  "(TestNumber, Steps) values (" +
			  "'" + testNumber + "', " +
			  "'" + testSteps + "' " +
			  ") ";
			  success = stmt.executeUpdate(sql);
		  }
		  conn.close();
	  } catch (InstantiationException e) {
		  exception = Boolean.TRUE;
	  } catch (IllegalAccessException e) {
		  exception = Boolean.TRUE;
	  } catch (ClassNotFoundException e) {
		  exception = Boolean.TRUE;
	  } catch (SQLException e) {
		  exception = Boolean.TRUE;
	  }
	  return exception;
}
}
