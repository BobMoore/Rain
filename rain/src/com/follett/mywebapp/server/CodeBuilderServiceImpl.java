package com.follett.mywebapp.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.stringtemplate.v4.*;

import com.follett.mywebapp.client.CodeBuilderService;
import com.follett.mywebapp.util.CodeContainer;
import com.follett.mywebapp.util.CodeStep;
import com.follett.mywebapp.util.DBNames;
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
		  Class.forName(DatabaseParameterBean.getTDSDriver()).newInstance();
	  } catch (InstantiationException e) {
		  e.printStackTrace();
	  } catch (IllegalAccessException e) {
		  e.printStackTrace();
	  } catch (ClassNotFoundException e) {
		  e.printStackTrace();
	  }
	  String url = DatabaseParameterBean.getDatabaseURL();
	  Connection conn;
	  try {
		  conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
		  Statement stmt = conn.createStatement();
		  String tag = "";
		  String table = DBNames.TABLE_SETUP;
		  boolean newStep = false;
		  while(!tagIDs.isEmpty()) {
			  if(tagIDs.contains(",")) {
				  tag = tagIDs.substring(0, tagIDs.indexOf(',')).trim();
				  tagIDs = tagIDs.substring(tagIDs.indexOf(',') + 1).trim();
			  } else {
				  tag = tagIDs.trim();
				  tagIDs = "";
			  }
			  if(tag.equals(DBNames.TABLE_VALIDATION)) {
				  table = DBNames.TABLE_VALIDATION;
			  } else if(tag.equals("New Step")) {
				  newStep = true;
			  }	else {
				  ResultSet rs = stmt.executeQuery("SELECT * FROM " + table + " WHERE " + DBNames.TAGID + " = '" + tag + "'");
				  if(rs.next()) {
					  StepTableData tempData = new StepTableData(
							  rs.getString(DBNames.TAGID),
							  rs.getString(DBNames.LABEL),
							  Integer.valueOf(rs.getInt(DBNames.TEXTFIELDS)),
							  true,
							  newStep);
					  if(table == DBNames.TABLE_VALIDATION) {
						  tempData.setSetup(false);
						  tempData.setNewStep(true);
					  }
					  tempData.addDescriptions(rs.getString(DBNames.FIELD_DESCRIPTIONS));
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
		  Class.forName(DatabaseParameterBean.getTDSDriver()).newInstance();
		  String url = DatabaseParameterBean.getDatabaseURL();
		  Connection conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
		  Statement stmt = conn.createStatement();
		  String sql = "";
		  sql = "UPDATE " + DBNames.TABLE_TESTS + " SET " +
		  DBNames.TESTS_STEPS + " = '" + testSteps + "' " +
		  "WHERE " + DBNames.TESTS_TEST_NUMBER + "= '" + testNumber + "'";
		  int success = stmt.executeUpdate(sql);
		  if(success == 0) {
			  sql = "INSERT INTO " + DBNames.TABLE_TESTS + " " +
			  "(" + DBNames.TESTS_TEST_NUMBER + ", " + DBNames.TESTS_STEPS + "Steps) values (" +
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
		  Class.forName(DatabaseParameterBean.getTDSDriver()).newInstance();
		  String url = DatabaseParameterBean.getDatabaseURL();
		  Connection conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
		  Statement stmt = conn.createStatement();
		  String sql = "SELECT COUNT(*) as \"TestExists\"\r\n" +
		  		"FROM " + DBNames.TABLE_TESTS + "\r\n" +
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
		  Class.forName(DatabaseParameterBean.getTDSDriver()).newInstance();
		  String url = DatabaseParameterBean.getDatabaseURL();
		  Connection conn = DriverManager.getConnection(url, DatabaseParameterBean.getConnectionProperties());
		  Statement stmt = conn.createStatement();
		  String sql = "SELECT " + DBNames.TESTS_STEPS + " FROM " + DBNames.TABLE_TESTS + "\r\n" +
		  		"WHERE " + DBNames.TESTS_TEST_NUMBER + " = " + testNumber + ";";
		  ResultSet rs = stmt.executeQuery(sql);
		  if(rs.next()) {
			  returnable = rs.getString(DBNames.TESTS_STEPS);
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
				  if(tag.getTitles() != null && tag.getParams() != null) {
					  int size = (tag.getTitles().size() < tag.getParams().size())? tag.getTitles().size(): tag.getParams().size();
					  for(int a = 0; a < size; a++) {
						  if(!(tag.getTitles().get(a).isEmpty() || tag.getParams().get(a).isEmpty())) {
							  attempt.add(tag.getTitles().get(a), tag.getParams().get(a));
						  }
					  }
				  }
				  label += attempt.render() + "\r\n";
			  }
		  }
	  }
	  return label;
  }
}
