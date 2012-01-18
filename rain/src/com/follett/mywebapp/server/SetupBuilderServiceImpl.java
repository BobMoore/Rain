package com.follett.mywebapp.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.follett.mywebapp.util.SetupDataItem;
import com.follett.mywebapp.util.TableData;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class SetupBuilderServiceImpl extends RemoteServiceServlet implements SetupBuilderService {

  private static final long serialVersionUID = 1L;

  /**
   * Implement the connection to the server and gather the necessary data
   * */
  @Override
  public SetupDataItem getSetupData() {
	  SetupDataItem returnable = new SetupDataItem();
	  try {
		  //send everything!
		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();

		  String url = "jdbc:sqlserver://127.0.0.1:1433;" +
		  "databaseName=Rain;user=sa;password=stuffy;";
		  Connection conn = DriverManager.getConnection(url);
		  Statement stmt = conn.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT * FROM dbo.Setup ORDER BY tab");
		  String column;
		  String tagID;
		  boolean checkbox;
		  String label;
		  Integer fields;
		  String tab;
		  String fieldDescriptions;
		  while(rs.next()) {
			  column = rs.getString("columnHeading");
			  tagID = rs.getString("tagID");
			  checkbox = rs.getBoolean("checkbox");
			  label = rs.getString("label");
			  fields = Integer.valueOf(rs.getInt("textfields"));
			  tab = rs.getString("tab");
			  fieldDescriptions = rs.getString("fieldDescriptions");
			  TableData data = new TableData(tagID, label, checkbox, fields);
			  data.addDescriptions(fieldDescriptions);
			  returnable.addTab(tab);
			  returnable.addColumnToTab(tab, column);
			  returnable.addDataToColumn(column, data);
		  }
		  conn.close();
	  } catch (SQLException e) {
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	  } catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  return returnable;
  }

  public Boolean saveSetupData(SetupDataItem allData) {
	  Boolean exception = Boolean.FALSE;
	  try {
		  Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver").newInstance();
		  String url = "jdbc:sqlserver://127.0.0.1:1433;" +
		  "databaseName=Rain;user=sa;password=stuffy;";
		  Connection conn = DriverManager.getConnection(url);
		  Statement stmt = conn.createStatement();
		  String sql = "";
		  ArrayList<String> tabs = allData.getTabs();
		  for (String tab : tabs) {
			  ArrayList<String> columns = allData.getColumnsOnTab(tab);
			  for (String column : columns) {
				  ArrayList<TableData> dataSet = allData.getDataforColumn(column);
				  if(dataSet != null) {
					  for (TableData item : dataSet) {
						  sql = "UPDATE dbo.Setup SET " +
						  "columnHeading = '" + column + "', " +
						  "checkbox = '" + item.isCheckbox() + "', " +
						  "label = '" + item.getLabel() + "', " +
						  "textfields = '" + item.getTextfields() + "', " +
						  "tab = '" + tab + "', " +
						  "fieldDescriptions = '" + item.getDescriptionsToString() + "' " +
						  "WHERE tagID = '" + item.getTagID() + "'";
						  int success = stmt.executeUpdate(sql);
						  if(success == 0) {
							  sql = "INSERT INTO dbo.Setup " +
							  "(columnHeading, tagID, checkbox, label, textfields, tab, fieldDescriptions) values (" +
							  "'" + column + "', " +
							  "'" + item.getTagID() + "', " +
							  "'" + item.isCheckbox() + "', " +
							  "'" + item.getLabel() + "', " +
							  "'" + item.getTextfields() + "', " +
							  "'" + tab + "', " +
							  "'" + item.getDescriptionsToString() + "' " +
							  ") ";
							  success = stmt.executeUpdate(sql);
						  }
					  }
				  }
			  }
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
