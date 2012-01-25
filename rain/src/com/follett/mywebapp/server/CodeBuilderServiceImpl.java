package com.follett.mywebapp.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.follett.mywebapp.tmp.*;

import org.stringtemplate.v4.*;

import com.follett.mywebapp.util.CodeContainer;
import com.follett.mywebapp.util.CodeStep;
import com.follett.mywebapp.util.SingleTag;
import com.follett.mywebapp.util.StepTableData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("unused")
public class CodeBuilderServiceImpl extends RemoteServiceServlet implements CodeBuilderService {

  private static final long serialVersionUID = 1L;
  /**
   * Implement the connection to the server and gather the necessary data
   * */
  @Override
  public ArrayList<StepTableData> getSetupPiece(String tagIDs) {
	  ArrayList<StepTableData> returnable = new ArrayList<StepTableData>();
	  try {
		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
	  } catch (InstantiationException e) {
		  e.printStackTrace();
	  } catch (IllegalAccessException e) {
		  e.printStackTrace();
	  } catch (ClassNotFoundException e) {
		  e.printStackTrace();
	  }
	  String url = "jdbc:sqlserver://127.0.0.1:1433;" +
	  "databaseName=Rain;user=sa;password=stuffy;";
	  Connection conn;
	  try {
		  conn = DriverManager.getConnection(url);
		  Statement stmt = conn.createStatement();
		  String tag = "";
		  String table = "Setup";
		  boolean newStep = false;
		  while(!tagIDs.isEmpty()) {
			  if(tagIDs.contains(",")) {
				  tag = tagIDs.substring(0, tagIDs.indexOf(',')).trim();
				  tagIDs = tagIDs.substring(tagIDs.indexOf(',') + 1).trim();
			  } else {
				  tag = tagIDs.trim();
				  tagIDs = "";
			  }
			  if(tag.equals("Validation")) {
				  table = "TreeItems";
			  } else if(tag.equals("New Step")) {
				  newStep = true;
			  }	else {
				  ResultSet rs = stmt.executeQuery("SELECT * FROM dbo." + table + " WHERE tagID = '" + tag + "'");
				  if(rs.next()) {
					  StepTableData tempData = new StepTableData(
							  rs.getString("tagID"),
							  rs.getString("label"),
							  Integer.valueOf(rs.getInt("textfields")),
							  true,
							  newStep);
					  if(table == "TreeItems") {
						  tempData.setSetup(false);
						  tempData.setNewStep(true);
					  }
					  tempData.addDescriptions(rs.getString("fieldDescriptions"));
					  returnable.add(tempData);
					  if(newStep == true) {
						  newStep = false;
					  }
				  }
			  }
		  }
	  } catch (SQLException e) {
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

  @Override
  public Boolean doesTestExist(int testNumber) {
	  Boolean exists = Boolean.FALSE;
	  try {
		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		  String url = "jdbc:sqlserver://127.0.0.1:1433;" +
		  "databaseName=Rain;user=sa;password=stuffy;";
		  Connection conn = DriverManager.getConnection(url);
		  Statement stmt = conn.createStatement();
		  String sql = "SELECT COUNT(*) as \"TestExists\"\r\n" +
		  		"FROM dbo.tests\r\n" +
		  		"WHERE TestNumber = " + testNumber + ";";
		  ResultSet rs = stmt.executeQuery(sql);
		  if(rs.next()) {
			  if(rs.getInt("TestExists") > 0) {
				  exists = Boolean.TRUE;
			  }
		  }
		  conn.close();
	  } catch (InstantiationException e) {
		  exists = Boolean.FALSE;
	  } catch (IllegalAccessException e) {
		  exists = Boolean.FALSE;
	  } catch (ClassNotFoundException e) {
		  exists = Boolean.FALSE;
	  } catch (SQLException e) {
		  exists = Boolean.FALSE;
	  }
	  return exists;
  }

  @Override
  public String getTest(int testNumber) {
	  String returnable = "";
	  try {
		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		  String url = "jdbc:sqlserver://127.0.0.1:1433;" +
		  "databaseName=Rain;user=sa;password=stuffy;";
		  Connection conn = DriverManager.getConnection(url);
		  Statement stmt = conn.createStatement();
		  String sql = "SELECT Steps FROM dbo.Tests\r\n" +
		  		"WHERE TestNumber = "+ testNumber +";";
		  ResultSet rs = stmt.executeQuery(sql);
		  if(rs.next()) {
			  returnable = rs.getString("Steps");
		  }
		  conn.close();
	  } catch (InstantiationException e) {
	  } catch (IllegalAccessException e) {
	  } catch (ClassNotFoundException e) {
	  } catch (SQLException e) {
	  }
	  return returnable;
  }

  @Override
  public String generateTemplatedCode(CodeContainer testCode) {
	  STGroup g = new STGroupFile("/com/follett/mywebapp/tmp/AllFile.stg");
	  String label = "";
	  ArrayList<SingleTag> tags;
	  for (CodeStep step : testCode.getStepList()) {
		  tags = new ArrayList<SingleTag>();
		  if(step.getMultiTag() != null) {
			  tags = step.getMultiTag();
		  } else {
			  tags.add(new SingleTag(step.getTagID(), step.getVariables(), step.getTitles()));
		  }
		  for (SingleTag tag : tags) {
			  ST attempt = g.getInstanceOf(tag.getTag());
			  if(attempt == null) {
				  label += "fail(\"Step " + tag.getTag() +" not implemented\");\r\n";
			  }else {
				  label += attempt.render() + "\r\n";
			  }
		  }
	  }
	  return label;
  }
}
